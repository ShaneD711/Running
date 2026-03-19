package com.shaned.running.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shaned.running.common.BusinessException;
import com.shaned.running.constant.ErrorCode;
import com.shaned.running.constant.RedisKey;
import com.shaned.running.dto.LoginRequest;
import com.shaned.running.dto.RegisterRequest;
import com.shaned.running.entity.User;
import com.shaned.running.mapper.UserMapper;
import com.shaned.running.util.JwtUtil;
import com.shaned.running.util.PasswordUtil;
import com.shaned.running.vo.LoginVO;
import com.shaned.running.vo.UserVO;import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务层：注册、登录、退出登录、查询用户信息
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 注册
     *
     * 1. 查库检查手机号是否已注册，已注册抛出业务异常
     * 2. BCrypt 加密密码后插入数据库
     * 3. 返回 UserVO（过滤掉 password 等敏感字段）
     *
     * 并发隐患：两个请求同时注册同一手机号时，selectOne 都查到"不存在"，
     * 都会执行 insert，数据库唯一索引会拦截第二个 insert 并抛出 DuplicateKeyException。
     */
    public UserVO register(RegisterRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User existUser = userMapper.selectOne(queryWrapper);

        if (existUser != null) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, ErrorCode.PHONE_ALREADY_EXISTS_MSG);
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(PasswordUtil.encodePassword(request.getPassword()));
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        log.info("用户注册成功: {}", request.getPhone());
        return UserVO.from(user);
    }

    /**
     * 登录
     *
     * 1. 根据手机号查库，不存在抛出业务异常
     * 2. BCrypt 验证密码，不匹配抛出业务异常
     * 3. 生成 JWT Token，连同用户信息一起返回
     */
    public LoginVO login(LoginRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND, ErrorCode.PHONE_NOT_FOUND_MSG);
        }

        // BCrypt 验证：从加密密码中提取盐值，对明文重新计算后比对
        if (!PasswordUtil.matchPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, ErrorCode.PASSWORD_ERROR_MSG);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        log.info("用户登录成功: {}", request.getPhone());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(UserVO.from(user));
        return loginVO;
    }

    /**
     * 退出登录
     *
     * JWT 本身无法主动失效，通过 Redis 黑名单实现：
     * 将 Token 写入 Redis，key 为 jwt:blacklist:<token>，
     * TTL 设为 Token 剩余有效期，到期后 Redis 自动清除，不会无限堆积。
     * 之后每次请求经过 JwtAuthFilter 时会查黑名单，命中则返回 401。
     */
    public void logout(String token) {
        long remaining = jwtUtil.getRemainingExpiration(token);
        if (remaining > 0) {
            redisTemplate.opsForValue().set(
                RedisKey.JWT_BLACKLIST + token, "1", remaining, TimeUnit.MILLISECONDS
            );
        }
        log.info("用户退出登录，Token 已加入黑名单");
    }

    /**
     * 检查 Token 是否在黑名单中，由 JwtAuthFilter 调用
     *
     * Boolean.TRUE.equals() 而不是直接 == true，
     * 因为 redisTemplate.hasKey() 返回 Boolean 对象可能为 null，直接拆箱会空指针
     */
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(RedisKey.JWT_BLACKLIST + token));
    }

    /**
     * 获取当前用户信息，由 UserController 的 /me 接口调用
     * userId 来自 JwtAuthFilter 写入的 UserContext，不需要从 Token 重新解析
     */
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            // Token 合法说明用户存在，走到这里属于异常情况（如用户被删除）
            throw new BusinessException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND_MSG);
        }
        return UserVO.from(user);
    }
}

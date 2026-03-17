package com.shaned.running.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shaned.running.common.BusinessException;
import com.shaned.running.constant.ErrorCode;
import com.shaned.running.dto.LoginRequest;
import com.shaned.running.dto.RegisterRequest;
import com.shaned.running.entity.User;
import com.shaned.running.mapper.UserMapper;
import com.shaned.running.util.JwtUtil;
import com.shaned.running.util.PasswordUtil;
import com.shaned.running.vo.LoginVO;
import com.shaned.running.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户业务层
 *
 * 作用：处理用户相关的业务逻辑，是整个功能的核心。
 * Controller 只负责接收请求，具体的业务逻辑都在 Service 中处理。
 *
 * 注册流程：
 * 1. 检查手机号是否已注册（查数据库）
 * 2. 已注册 → 抛出业务异常
 * 3. 未注册 → BCrypt加密密码 → 插入数据库 → 返回UserVO
 *
 * 登录流程：
 * 1. 根据手机号查询用户（查数据库）
 * 2. 用户不存在 → 抛出业务异常
 * 3. 用户存在 → BCrypt验证密码
 * 4. 密码错误 → 抛出业务异常
 * 5. 密码正确 → 生成JWT Token → 返回LoginVO
 */
@Slf4j
@Service
public class UserService {

    // 注入UserMapper，用于操作数据库
    @Autowired
    private UserMapper userMapper;

    // 注入JwtUtil，用于生成Token
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * @param request 注册请求（手机号、密码）
     * @return UserVO 注册成功的用户信息（不含密码）
     */
    public UserVO register(RegisterRequest request) {
        // 第一步：检查手机号是否已注册
        // QueryWrapper 是 MyBatisPlus 的条件构造器，eq("phone", ...) 相当于 WHERE phone = ?
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            // 手机号已存在，抛出业务异常，GlobalExceptionHandler 会捕获并返回错误响应
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, ErrorCode.PHONE_ALREADY_EXISTS_MSG);
        }

        // 第二步：创建新用户并保存到数据库
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(PasswordUtil.encodePassword(request.getPassword())); // 密码加密后存储
        user.setStatus(1); // 默认状态：正常
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        log.info("用户注册成功: {}", request.getPhone());

        // 第三步：将 User 转换为 UserVO 返回（去掉密码等敏感字段）
        return convertToUserVO(user);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（手机号、密码）
     * @return LoginVO 登录成功的Token和用户信息
     */
    public LoginVO login(LoginRequest request) {
        // 第一步：根据手机号查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.PHONE_NOT_FOUND, ErrorCode.PHONE_NOT_FOUND_MSG);
        }

        // 第二步：验证密码（BCrypt会自动处理盐值，直接比对即可）
        if (!PasswordUtil.matchPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, ErrorCode.PASSWORD_ERROR_MSG);
        }

        // 第三步：生成JWT Token，包含用户ID和手机号
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        log.info("用户登录成功: {}", request.getPhone());

        // 第四步：组装返回数据
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(convertToUserVO(user));

        return loginVO;
    }

    /**
     * 将 User 实体转换为 UserVO
     * 作用：过滤掉 password 等敏感字段，只返回前端需要的数据
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setPhone(user.getPhone());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        return userVO;
    }
}

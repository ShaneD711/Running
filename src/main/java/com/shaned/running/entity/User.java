package com.shaned.running.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * 作用：与数据库的 user 表一一对应，每个字段对应表中的一列。
 * MyBatisPlus 通过这个类自动生成 SQL，不需要手写 SQL 语句。
 *
 * 注意：这个类包含 password 字段，不能直接返回给前端！
 * 需要转换成 UserVO（去掉敏感字段）再返回。
 *
 * @TableName("user") 指定对应的数据库表名
 * @TableId(type = IdType.AUTO) 指定主键自增
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    // 用户ID，主键，自增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 手机号，唯一，用于登录
    private String phone;

    // 密码，BCrypt加密后存储，不存明文
    private String password;

    // 昵称，用户可自定义
    private String nickname;

    // 头像URL，存储图片地址
    private String avatar;

    // 账户状态：1=正常，0=禁用
    private Integer status;

    // 创建时间，注册时自动记录
    private LocalDateTime createdAt;

    // 更新时间，每次修改自动更新
    private LocalDateTime updatedAt;
}

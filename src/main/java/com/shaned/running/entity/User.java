package com.shaned.running.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体，对应数据库 user 表
 *
 * 包含 password 字段，不能直接返回给前端，需转换为 UserVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO) // 主键自增，插入后自动回填 id
    private Long id;

    private String phone;     // 手机号，唯一索引，用于登录
    private String password;  // BCrypt 加密后的密码，不存明文
    private String nickname;
    private String avatar;    // 头像 URL
    private Integer status;   // 1=正常，0=禁用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

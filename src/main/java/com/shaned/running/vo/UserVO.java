package com.shaned.running.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户视图对象
 *
 * 作用：返回给前端的用户信息，只包含安全的字段。
 * VO（View Object）= 视图对象，专门用来返回给前端的数据。
 *
 * 为什么不直接返回 User 实体类？
 * - User 包含 password 字段，直接返回会泄露密码
 * - User 包含 status、updatedAt 等前端不需要的字段
 * - 用 VO 只返回需要的字段，更安全
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    // 用户ID
    private Long id;

    // 手机号
    private String phone;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;
}

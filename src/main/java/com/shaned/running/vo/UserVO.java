package com.shaned.running.vo;

import com.shaned.running.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户视图对象，返回给前端的用户信息
 *
 * 不直接返回 User 实体的原因：User 包含 password 等敏感字段，不能暴露给前端。
 * 使用场景：注册响应、登录响应（LoginVO.user）、GET /user/me
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String phone;
    private String nickname;
    private String avatar;

    // 将 User 实体转换为 UserVO，过滤掉 password 等敏感字段
    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}

# Running 版本记录

## v0.2.0 - 计划中

### 功能
- JWT 拦截器（验证请求头中的 Token，保护需要登录的接口）
- 退出登录（将 Token 存入 Redis 黑名单，实现主动失效）

### API
- `POST /api/v1/auth/logout` 退出登录

### 技术要点
- JWT + Redis 黑名单：解决 JWT 无法主动失效的问题
- 退出登录时将 Token 存入 Redis，过期时间与 Token 剩余有效期一致
- 每次请求先验证签名，再查 Redis 黑名单，双重验证

---

## v0.1.0 - 2026-03-17

### 完成内容
用户注册登录功能，包含基础框架搭建。

### 功能
- 手机号注册（BCrypt加密密码）
- 手机号登录

### API
- `POST /api/v1/auth/register` 注册
- `POST /api/v1/auth/login` 登录

### 技术要点
- BCrypt 密码加密，防止数据库泄露后密码被破解
- 参数校验：手机号格式、密码长度至少 6 位
- 全局异常处理，统一响应格式

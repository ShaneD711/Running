# Running 版本记录

---

## v0.2.4 - 待实现

### 概述
解决 JWT 修改密码后仍可访问的安全问题，通过 Token 版本号机制使旧 Token 立即失效。

### 问题背景
- 用户修改密码后，旧 JWT 在过期前仍然有效
- 安全风险：账号泄露后修改密码，攻击者仍可用旧 Token 访问
- JWT 无状态特性导致无法主动撤销

### 解决方案：Token 版本号

#### 数据库变更
- `user` 表新增 `token_version INT DEFAULT 1` 字段
- 修改密码时递增版本号

#### 实现逻辑
1. **登录时**：JWT Payload 包含 `tokenVersion`
   ```json
   {
     "sub": "123",
     "tokenVersion": 1,
     "iat": 1234567890,
     "exp": 1234654290
   }
   ```

2. **修改密码时**：
   - 更新密码
   - `token_version` 递增（1 → 2）
   - 可选：将用户 ID 写入 Redis 黑名单（双重保险）

3. **验证 Token 时**（JwtAuthFilter）：
   - 解析 JWT 获取 `tokenVersion`
   - 查询数据库获取 `user.token_version`
   - 对比版本号：不匹配 → 返回 401

#### 新增文件
- `dto/ChangePasswordRequest.java` — 修改密码请求参数
- `constant/ErrorCode.java` — 新增错误码 `TOKEN_VERSION_MISMATCH`

#### 修改文件
- `entity/User.java` — 新增 `tokenVersion` 字段
- `util/JwtUtil.java` — `generateToken()` 包含版本号，新增 `getTokenVersion()` 方法
- `service/UserService.java` — 新增 `changePassword()` 方法，登录时传入版本号
- `config/JwtAuthFilter.java` — 验证时检查版本号
- `controller/UserController.java` — 新增修改密码接口 `PUT /api/v1/user/password`
- `resources/db/schema.sql` — 添加 `token_version` 字段

#### 新增接口
- `PUT /api/v1/user/password` — 修改密码（需要 Token）
  - 请求参数：`oldPassword`, `newPassword`
  - 验证旧密码正确
  - 更新密码并递增版本号
  - 返回成功，客户端需重新登录

### 技术要点
- 版本号机制：简单高效，只需一次数据库查询（验证时本来就要查用户信息）
- 修改密码后旧 Token 立即失效，无需等待过期
- 兼容现有退出登录的 Redis 黑名单机制
- 数据库迁移：`ALTER TABLE user ADD COLUMN token_version INT DEFAULT 1 COMMENT 'Token版本号';`

### 安全性提升
- ✅ 修改密码后旧 Token 立即失效
- ✅ 防止账号泄露后的持续访问
- ✅ 用户可主动撤销所有登录会话（通过修改密码）

---

## v0.2.3 - 2026-03-23

### 概述
完善日志管理系统，新增文件输出、滚动策略和敏感信息脱敏，满足小型项目生产环境需求。

### 新增文件
- `resources/logback-spring.xml` — Logback 日志配置，支持按环境切换、文件滚动、错误日志单独记录
- `util/LogUtil.java` — 日志脱敏工具类，提供手机号、邮箱、身份证、Token 脱敏方法

### 修改文件
- `resources/application.yml` — 简化日志配置，日志路径交由 logback-spring.xml 管理
- `service/UserService.java` — 日志中手机号脱敏（`138****8000`），增加 userId 便于追踪，Token 脱敏记录
- `.gitignore` — 忽略 logs/ 目录

### 日志输出
- **开发环境**：控制台 + 文件（`./logs/`）
- **生产环境**：仅文件（建议 `/var/log/running/`）
- **文件结构**：
  - `running.log` — 所有日志（INFO 及以上）
  - `running-error.log` — 错误日志单独记录
  - `running-2026-03-23.0.log` — 按日期滚动的历史日志

### 滚动策略
- 按日期滚动：每天生成新文件
- 按大小分片：单文件超过 100MB 自动分片（`.0.log`, `.1.log`）
- 保留时间：普通日志 30 天，错误日志 90 天
- 总大小限制：最多 10GB，超过后删除最旧文件

### 日志格式
```
2026-03-23 10:15:32.123 [http-nio-8080-exec-1] INFO  c.s.running.service.UserService - 用户登录成功: userId=123, phone=138****8000
```

### 敏感信息脱敏
- 手机号：`13800138000` → `138****8000`
- Token：只记录前后 8 位，避免泄露
- 邮箱：`user@example.com` → `us****@example.com`
- 身份证：保留前 6 位和后 4 位

### 技术要点
- 使用 Logback 作为日志框架（Spring Boot 默认）
- 支持通过 `--spring.profiles.active=prod` 切换环境
- 日志级别：开发环境 DEBUG，生产环境 INFO
- 适用场景：单机部署，日访问量 < 10 万

---

## v0.2.2 - 2026-03-23

### 概述
移除 JWT Payload 中的敏感信息，提升安全性。

### 修改文件
- `util/JwtUtil.java` — 移除 `phone` 参数和 `getPhone()` 方法，Payload 仅保留 `userId`
- `service/UserService.java` — 调用 `generateToken()` 时移除 `phone` 参数

### 技术要点
- JWT Payload 使用 Base64 编码（非加密），客户端可直接解码查看内容
- 手机号属于敏感个人信息，不应存储在可被解码的 Payload 中
- 移除后 Payload 仅包含：`sub`(userId)、`iat`(签发时间)、`exp`(过期时间)
- Token 体积减小，安全性提升，需要用户信息时通过 `userId` 查库获取

---

## v0.2.1 - 2026-03-20

### 概述
重构密码工具类，移除静态工具类反模式。

### 新增文件
- `config/PasswordEncoderConfig.java` — 独立 `PasswordEncoder` Bean 配置

### 修改文件
- `service/UserService.java` — 直接注入 `PasswordEncoder`，替换 `PasswordUtil` 静态调用
- `config/SecurityConfig.java` — 移除 `PasswordEncoder` Bean，职责收归 `PasswordEncoderConfig`
- `util/PasswordUtil.java` — 删除

---

## v0.2.0 - 2026-03-18

### 概述
在 v0.1.0 基础上补全 JWT 鉴权体系。v0.1.0 只生成 Token，没有验证 Token 的过滤器，v0.2.0 新增请求拦截、Redis 黑名单退出登录和获取当前用户信息接口。

### 新增文件
- `config/JwtAuthFilter.java` — JWT 认证过滤器，拦截所有请求验证 Token，写入 UserContext
- `util/UserContext.java` — ThreadLocal 用户上下文，存储当前请求的用户 ID
- `controller/UserController.java` — 用户信息接口
- `constant/RedisKey.java` — Redis key 常量

### 修改文件
- `config/SecurityConfig.java` — 合并 WebSecurityConfig，注册 JwtAuthFilter
- `config/WebSecurityConfig.java` — 删除，职责合并至 SecurityConfig
- `controller/AuthController.java` — 新增 logout 接口
- `service/UserService.java` — 新增 logout、isTokenBlacklisted、getCurrentUser 方法，删除 convertToUserVO()
- `util/JwtUtil.java` — 新增 getRemainingExpiration() 方法
- `vo/UserVO.java` — 新增 from(User) 静态方法，统一 User → UserVO 的转换逻辑

### 新增接口
| 方法 | 路径                    | 说明             | 需要 Token |
|------|-------------------------|------------------|-----------|
| POST | `/api/v1/auth/logout`   | 退出登录         | 是        |
| GET  | `/api/v1/user/me`       | 获取当前用户信息 | 是        |

### 技术要点
- JWT 无法主动失效，退出登录时将 Token 写入 Redis 黑名单（key: `jwt:blacklist:<token>`），TTL 与 Token 剩余有效期一致，到期自动清除
- 每次请求：验签 → 查黑名单 → 写 UserContext，三步顺序执行
- ThreadLocal 在 finally 块中清理，防止线程池复用时数据污染

---

## v0.1.0 - 2026-03-17

### 概述
项目初始版本，搭建 Spring Boot 基础框架，实现用户注册和登录功能。

### 新增文件
- `entity/User.java` — 用户实体，对应 user 表
- `mapper/UserMapper.java` — 继承 BaseMapper，提供 CRUD 方法
- `service/UserService.java` — 注册、登录业务逻辑
- `controller/AuthController.java` — 注册、登录接口
- `config/SecurityConfig.java` — 注册 BCryptPasswordEncoder Bean
- `config/WebSecurityConfig.java` — Spring Security 放行规则
- `util/JwtUtil.java` — JWT 生成与验证
- `util/PasswordUtil.java` — BCrypt 密码加密与校验
- `common/Result.java` — 统一响应格式
- `common/BusinessException.java` — 业务异常类
- `common/GlobalExceptionHandler.java` — 全局异常处理
- `constant/ErrorCode.java` — 错误码常量
- `dto/RegisterRequest.java` — 注册请求参数，含校验规则
- `dto/LoginRequest.java` — 登录请求参数
- `vo/UserVO.java` — 用户视图对象，过滤敏感字段
- `vo/LoginVO.java` — 登录响应，包含 Token 和用户信息
- `resources/db/schema.sql` — 数据库建表语句

### 新增接口
| 方法 | 路径                      | 说明               | 需要 Token |
|------|---------------------------|--------------------|-----------|
| POST | `/api/v1/auth/register`   | 用户注册           | 否        |
| POST | `/api/v1/auth/login`      | 用户登录，返回 Token | 否       |

### 技术要点
- BCrypt 加密密码，随机盐值，防彩虹表攻击
- JWT HS256 签名，Payload 含用户 ID 和手机号，有效期 24 小时
- 参数校验：手机号正则 `^1[3-9]\d{9}$`，密码最少 6 位，失败返回 400
- 统一响应格式 `Result<T>`，错误码分段管理（通用 400~500，用户模块 1001~1999）
- 全局异常处理，Service 层直接 throw，不需要在 Controller 写 try-catch

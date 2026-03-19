# Running 版本记录

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

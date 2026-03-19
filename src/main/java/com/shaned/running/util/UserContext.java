package com.shaned.running.util;

/**
 * 用户上下文：在同一请求中存取当前登录用户的 ID
 *
 * 使用场景：
 *   JwtAuthFilter 验证 Token 后调用 setUserId() 存入用户 ID
 *   Controller/Service 层调用 getUserId() 取用，无需将 userId 作为参数层层传递
 *
 * 为什么用 ThreadLocal：
 *   Web 服务器用线程池处理请求，每个请求分配一个线程。
 *   ThreadLocal 让每个线程有自己独立的变量副本，线程 A 存入的数据线程 B 读不到，天然隔离。
 *
 * 为什么必须调用 clear()：
 *   线程处理完请求后不会销毁，而是回到线程池等待下一个请求复用。
 *   如果不清理，下一个请求复用这个线程时会读到上一个用户的 ID，造成数据错乱。
 *   JwtAuthFilter 在 finally 块中调用 clear()，保证无论请求是否正常结束都会清理。
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    // JwtAuthFilter 在 Token 验证通过后调用
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    // Controller/Service 层调用，获取当前请求的用户 ID
    public static Long getUserId() {
        return USER_ID.get();
    }

    // 用 remove() 而不是 set(null)，彻底释放内存，防止内存泄漏
    public static void clear() {
        USER_ID.remove();
    }
}

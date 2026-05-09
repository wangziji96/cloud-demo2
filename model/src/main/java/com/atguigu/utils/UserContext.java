package com.atguigu.utils;

import com.atguigu.user.LoginUser;

public class UserContext {
    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(LoginUser user) {
        USER_HOLDER.set(user);
    }

    public static LoginUser getUser() {
        return USER_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
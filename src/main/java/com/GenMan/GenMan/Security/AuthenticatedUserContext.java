package com.GenMan.GenMan.Security;

public class AuthenticatedUserContext {

    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}

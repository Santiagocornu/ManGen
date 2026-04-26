package com.GenMan.GenMan.Security;

public class SucursalContext {

    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();

    public static void set(Long sucursalId) {
        CURRENT.set(sucursalId);
    }

    public static Long get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}

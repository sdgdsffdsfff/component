package com.hehua.framework.web.model;

public enum Platform {
    iOS(0), //
    Android(1), //
    ;

    private final int value;

    /**
     * @param value
     */
    private Platform(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final java.util.Map<Integer, Platform> map = new java.util.HashMap<>();
    static {
        for (Platform e : Platform.values()) {
            map.put(e.getValue(), e);
        }
    }

    public static final Platform fromValue(int status) {
        return map.get(status);
    }
}

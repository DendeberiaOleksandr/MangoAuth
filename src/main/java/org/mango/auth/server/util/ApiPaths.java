package org.mango.auth.server.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiPaths {
    private static final String API_BASE = "/api/v1";

    public static final String USER_API = API_BASE + "/users";
    public static final String SIGN_UP = API_BASE + "/sign-up";
    public static final String TOKEN = API_BASE + "/token";
    public static final String TOKEN_REFRESH = TOKEN + "/refresh";
    public static final String TOKEN_SIGN_OUT = TOKEN + "/sign-out";
}

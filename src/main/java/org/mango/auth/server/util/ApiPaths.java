package org.mango.auth.server.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiPaths {
    private static final String API_BASE = "/api/v1"; // Базовая часть пути

    public static final String SIGN_UP = API_BASE + "/sign-up";
    public static final String TOKEN = API_BASE + "/token";

}

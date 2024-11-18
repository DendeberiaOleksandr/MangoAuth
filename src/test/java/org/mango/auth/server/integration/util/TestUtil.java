package org.mango.auth.server.integration.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestUtil {

    public static final UUID CLIENT_ID_1 = UUID.fromString("9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9");
    public static final String CLIENT_NAME_1 = "Client 1";
    public static final String CLIENT_API_KEY_1 = "apiKey";
    public static final UUID CLIENT_ID_2 = UUID.fromString("8d4e2b7b-d9a1-4d82-b5e1-00cfb88d417d");
    public static final String USER_EMAIL = "testUser1110@example.com";
    public static final String USER_PASSWORD = "testUser1110";
    public static final UUID USER_ID = UUID.fromString("a3dc28ce-39e5-4797-8d7a-f6809b6f1f03");
    public static final UUID ADMIN_USER_ID = UUID.fromString("7ed1259c-aee0-4ce7-b297-f9a090b2735a");
    public static final String ADMIN_USER_EMAIL = "testAdmin1121@example.com";

}

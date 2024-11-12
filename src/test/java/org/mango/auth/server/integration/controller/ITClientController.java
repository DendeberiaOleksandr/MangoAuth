package org.mango.auth.server.integration.controller;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.integration.ITBase;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mango.auth.server.integration.util.TestUtil.ADMIN_USER_EMAIL;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID_1;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_NAME_1;
import static org.mango.auth.server.integration.util.TestUtil.USER_EMAIL;
import static org.mango.auth.server.util.ApiPaths.CLIENT_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITClientController extends ITBase {

    @Test
    void getUserClientsWhereIsAdminOrOwner() throws Exception {
        mvc.perform(
                get(CLIENT_API)
                        .param("email", ADMIN_USER_EMAIL)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId", is(CLIENT_ID_1.toString())))
                .andExpect(jsonPath("$[0].clientName", is(CLIENT_NAME_1)))
                .andExpect(jsonPath("$[0].role", is(Role.ADMIN.name())))
                .andDo(print());
    }

    @Test
    void getUserClientsWhereIsAdminOrOwner_whenHasUserRoleOnly() throws Exception {
        mvc.perform(
                        get(CLIENT_API)
                                .param("email", USER_EMAIL)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()))
                .andDo(print());
    }

}

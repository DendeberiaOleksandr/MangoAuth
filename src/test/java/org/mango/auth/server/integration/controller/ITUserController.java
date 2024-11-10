package org.mango.auth.server.integration.controller;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.integration.ITBase;
import org.mango.auth.server.repository.ClientRepository;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mango.auth.server.integration.util.TestUtil.CLIENT_ID;
import static org.mango.auth.server.util.ApiPaths.USER_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITUserController extends ITBase {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserClientRoleRepository userClientRoleRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void searchUsers_whenClientIdNotProvided() throws Exception {
        // given
        final int page = 0;
        final int size = 10;

        // when
        ResultActions result = mvc.perform(
                        get(USER_API)
                                .param("page", page + "")
                                .param("size", size + "")
                )
                .andDo(print());

        // then
        result
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchUsers_whenUserExists() throws Exception {
        // given
        final int page = 0;
        final int size = 10;

        UserClientRole savedUser = createUser();

        // when
        ResultActions result = mvc.perform(
                        get(USER_API)
                                .param("page", page + "")
                                .param("size", size + "")
                                .param("clientId", CLIENT_ID.toString())
                )
                .andDo(print());

        // then
        User user = savedUser.getUser();
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber", is(page)))
                .andExpect(jsonPath("$.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.content[0]", notNullValue()))
                .andExpect(jsonPath("$.content[0].id", is(user.getId().toString())))
                .andExpect(jsonPath("$.content[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.content[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.content[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.content[0].role", is(savedUser.getRole().name())))
                .andExpect(jsonPath("$.content[0].userStatus", is(user.getUserStatus().name())))
                .andExpect(jsonPath("$.content[0].registeredAt", not(emptyString())));
    }

    @Test
    void searchUsers_whenUserExistsAndOffset() throws Exception {
        // given
        final int page = 1;
        final int size = 10;

        UserClientRole savedUser = createUser();

        // when
        ResultActions result = mvc.perform(
                        get(USER_API)
                                .param("page", page + "")
                                .param("size", size + "")
                                .param("clientId", CLIENT_ID.toString())
                )
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber", is(page)))
                .andExpect(jsonPath("$.pageable.pageSize", is(size)))
                .andExpect(jsonPath("$.content", empty()));
    }

    private UserClientRole createUser() {
        User user = Instancio.of(User.class)
                .set(Select.field(User::getId), null)
                .set(Select.field(User::getClientRoles), List.of())
                .create();
        userRepository.save(user);

        Client client = clientRepository.findById(CLIENT_ID).get();

        return userClientRoleRepository.save(UserClientRole.builder()
                .role(Role.USER)
                .client(client)
                .user(user)
                .build());
    }

}

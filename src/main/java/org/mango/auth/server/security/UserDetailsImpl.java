package org.mango.auth.server.security;


import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.enums.AccountType;
import org.mango.auth.server.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Client client;
    private final Role role;
    private final AccountType accountType;

    public UserDetailsImpl(User user, Client client, Role role) {
        this(user, client, role, AccountType.USER);
    }

    public UserDetailsImpl(User user, Client client, Role role, AccountType accountType) {
        this.user = user;
        this.client = client;
        this.role = role;
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public User getUser() {
        return user;
    }

    public Client getClient() {
        return client;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail(){
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role::name);
    }

    @Override
    public String getPassword() {
        if (accountType.equals(AccountType.USER)) {
            return user.getPassword();
        }
        return client.getSecretKey();
    }

    @Override
    public String getUsername() {
        if (accountType.equals(AccountType.USER)) {
            return user.getEmail() + "--" + client.getId();
        }
        return client.getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

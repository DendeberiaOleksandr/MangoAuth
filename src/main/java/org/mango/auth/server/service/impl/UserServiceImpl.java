package org.mango.auth.server.service.impl;

import lombok.AllArgsConstructor;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.repository.UserRepository;
import org.mango.auth.server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}

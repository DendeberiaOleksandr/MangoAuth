package org.mango.auth.server.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class KeyGeneratorServiceImplTest {

    KeyGeneratorServiceImpl service = new KeyGeneratorServiceImpl();

    @Test
    void generate() {
        String key = service.generate();
        assertNotNull(key);
        assertFalse(key.isBlank());
    }

}
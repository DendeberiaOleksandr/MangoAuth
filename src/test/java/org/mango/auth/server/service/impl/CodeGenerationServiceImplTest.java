package org.mango.auth.server.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CodeGenerationServiceImplTest {

    static final String ONLY_DIGITS_PATTERN = "^[0-9]{6}$";

    CodeGenerationServiceImpl service = new CodeGenerationServiceImpl();

    @Test
    void generate() {
        service.setCodeLength(6);

        String generate = service.generate();

        assertNotNull(generate);
        assertTrue(generate.matches(ONLY_DIGITS_PATTERN));
    }

}
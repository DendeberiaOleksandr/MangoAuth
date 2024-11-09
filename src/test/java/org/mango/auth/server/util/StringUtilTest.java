package org.mango.auth.server.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StringUtilTest {

    @Test
    void replaceByPattern() {
        final String html = """
                <html>
                    <h1>${EMAIL}</h1>
                    <h1>${CLIENT_NAME}</h1>
                </html>
                """;

        final String expected = """
                <html>
                    <h1>test</h1>
                    <h1>client</h1>
                </html>
                """;

        String result = StringUtil.replaceByPattern(html, Map.of(
                "EMAIL", "test",
                "CLIENT_NAME", "client"
        ));
        assertEquals(expected, result);
    }

}
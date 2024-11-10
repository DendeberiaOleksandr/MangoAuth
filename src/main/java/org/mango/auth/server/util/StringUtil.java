package org.mango.auth.server.util;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class StringUtil {

    public static String replaceByPattern(String string, Map<String, String> replacementMap) {
        AtomicReference<String> result = new AtomicReference<>(string);
        replacementMap.forEach((key, value) -> result.set(result.get().replace("${%s}".formatted(key), value)));
        return result.get();
    }

}

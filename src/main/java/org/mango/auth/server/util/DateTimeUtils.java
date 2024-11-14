package org.mango.auth.server.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class DateTimeUtils {
    public static LocalDateTime convertMillisToLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
}

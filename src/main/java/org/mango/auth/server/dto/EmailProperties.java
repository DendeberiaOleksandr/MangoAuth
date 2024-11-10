package org.mango.auth.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailProperties {
    private String subject;
    private String to;
    private String htmlContent;
}

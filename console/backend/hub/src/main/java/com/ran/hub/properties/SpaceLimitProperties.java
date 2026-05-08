package com.ran.hub.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "space.limit")
public class SpaceLimitProperties {

    private LimitConfig free = new LimitConfig();
    private LimitConfig pro = new LimitConfig();
    private LimitConfig team = new LimitConfig();
    private LimitConfig enterprise = new LimitConfig();

    @Data
    public static class LimitConfig {
        private Integer spaceCount = 10;
        private Integer userCount = 5;
    }
}

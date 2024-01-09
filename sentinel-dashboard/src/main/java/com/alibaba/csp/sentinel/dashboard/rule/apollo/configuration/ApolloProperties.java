package com.alibaba.csp.sentinel.dashboard.rule.apollo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wenming.zhang
 */
@Data
@ConfigurationProperties(prefix = "apollo")
public class ApolloProperties {

    private String portalUrl;

    private String openApiToken;

    private String operator = "sentinel";

    private String env;

    private String cluster = "default";

}
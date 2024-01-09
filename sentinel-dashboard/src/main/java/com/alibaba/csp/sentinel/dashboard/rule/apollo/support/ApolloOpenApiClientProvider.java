package com.alibaba.csp.sentinel.dashboard.rule.apollo.support;

import com.alibaba.csp.sentinel.dashboard.rule.apollo.configuration.ApolloProperties;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author wenming.zhang
 */
public class ApolloOpenApiClientProvider {

    @Autowired
    private ApolloProperties apolloProperties;

    private ApolloOpenApiClient client;

    @PostConstruct
    public void init() {
        modify(apolloProperties.getPortalUrl(), apolloProperties.getOpenApiToken());
    }

    public ApolloOpenApiClient get() {
        return client;
    }

    private ApolloOpenApiClient modify(String portalUrl, String token) {
        this.client = newClient(portalUrl, token);
        return get();
    }

    private ApolloOpenApiClient newClient(String portalUrl, String token) {
        return ApolloOpenApiClient.newBuilder()
                .withPortalUrl(portalUrl)
                .withToken(token)
                .build();
    }

    @ApolloConfigChangeListener
    private void setApolloPropertiesChangeListener(ConfigChangeEvent changeEvent) {
        if (changeEvent.isChanged("apollo.portalUrl") || changeEvent.isChanged("apollo.token")) {
            modify(apolloProperties.getPortalUrl(), apolloProperties.getOpenApiToken());
        }
    }

}

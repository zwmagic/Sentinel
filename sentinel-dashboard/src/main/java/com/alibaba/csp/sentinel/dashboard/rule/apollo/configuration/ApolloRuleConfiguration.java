package com.alibaba.csp.sentinel.dashboard.rule.apollo.configuration;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.aop.SentinelApiClientAspect;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.handler.ApolloDynamicRuleHandler;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.handler.DynamicRuleHandler;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.handler.DynamicRuleHandlerFactory;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.ApolloOpenApiClientProvider;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author wenming.zhang
 */
@Configuration
@EnableConfigurationProperties(ApolloProperties.class)
public class ApolloRuleConfiguration {

    @Autowired
    private ApolloProperties apolloProperties;

    @Bean
    public ApolloOpenApiClientProvider apolloOpenApiClientProvider() {
        return new ApolloOpenApiClientProvider();
    }

    @Bean
    public DynamicRuleHandler<FlowRuleEntity> flowDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.FLOW, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<DegradeRuleEntity> degradeDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.DEGRADE, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<ParamFlowRuleEntity> paramFlowDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.PARAM_FLOW, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<SystemRuleEntity> systemDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.SYSTEM, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<AuthorityRuleEntity> authorityDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.AUTHORITY, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<AuthorityRuleEntity> gatewayFlowDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.GATEWAY_FLOW, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandler<AuthorityRuleEntity> gatewayApiDynamicRuleHandler(ApolloOpenApiClientProvider apolloOpenApiClientProvider) {
        return new ApolloDynamicRuleHandler<>(RuleTypeEnum.GATEWAY_API, apolloProperties, apolloOpenApiClientProvider);
    }

    @Bean
    public DynamicRuleHandlerFactory dynamicRuleHandlerFactory(List<DynamicRuleHandler<? extends RuleEntity>> handlers) {
        return new DynamicRuleHandlerFactory(handlers);
    }

    @Bean
    public SentinelApiClientAspect sentinelApiClientAspect() {
        return new SentinelApiClientAspect();
    }

}
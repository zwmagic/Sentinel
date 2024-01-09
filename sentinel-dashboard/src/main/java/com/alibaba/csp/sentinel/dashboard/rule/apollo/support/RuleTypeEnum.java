package com.alibaba.csp.sentinel.dashboard.rule.apollo.support;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wenming.zhang
 */
public enum RuleTypeEnum {

    /**
     * 流控规则
     */
    FLOW("flow", FlowRuleEntity.class),
    /**
     * 熔断规则
     */
    DEGRADE("degrade", DegradeRuleEntity.class),
    /**
     * 热点规则
     */
    PARAM_FLOW("param-flow", ParamFlowRuleEntity.class),
    /**
     * 系统规则
     */
    SYSTEM("system", SystemRuleEntity.class),
    /**
     * 授权规则
     */
    AUTHORITY("authority", AuthorityRuleEntity.class),
    /**
     * 网关流控规则
     */
    GATEWAY_FLOW("gateway-flow", GatewayFlowRuleEntity.class),
    /**
     * gateway api
     */
    GATEWAY_API("gateway-api", ApiDefinitionEntity.class);

    ;

    private final String name;

    private final Class<? extends RuleEntity> clazz;

    RuleTypeEnum(String name, Class<? extends RuleEntity> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public <T extends RuleEntity> Class<T> getClazz() {
        return (Class<T>) clazz;
    }

    public static Optional<RuleTypeEnum> of(String name) {
        if (StringUtils.isEmpty(name)) {
            return Optional.empty();
        }
        return Arrays.stream(RuleTypeEnum.values())
                .filter(ruleType -> name.equals(ruleType.getName())).findFirst();
    }


}

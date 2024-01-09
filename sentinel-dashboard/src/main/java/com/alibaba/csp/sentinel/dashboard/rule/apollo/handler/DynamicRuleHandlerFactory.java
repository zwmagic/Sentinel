package com.alibaba.csp.sentinel.dashboard.rule.apollo.handler;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenming.zhang
 */
public class DynamicRuleHandlerFactory {

    private final Map<RuleTypeEnum, DynamicRuleHandler<? extends RuleEntity>> handlers;

    public DynamicRuleHandlerFactory(final List<DynamicRuleHandler<? extends RuleEntity>> handlerList) {
        handlers = new HashMap<>(handlerList.size());
        handlerList.forEach(handler -> handlers.putIfAbsent(handler.getRuleType(), handler));
    }

    public <T extends RuleEntity> DynamicRuleHandler<T> getHandler(final RuleTypeEnum ruleType) {
        DynamicRuleHandler<T> handler = (DynamicRuleHandler<T>) handlers.get(ruleType);
        if (handler == null) {
            throw new RuntimeException("can not find DynamicRuleHandler by type: " + ruleType.getName());
        }
        return handler;
    }

}

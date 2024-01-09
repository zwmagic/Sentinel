package com.alibaba.csp.sentinel.dashboard.rule.apollo.handler;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleTypeEnum;

import java.util.List;

/**
 * @author wenming.zhang
 */
public abstract class DynamicRuleHandler<T extends RuleEntity> implements DynamicRuleProvider<List<T>>,
        DynamicRulePublisher<List<T>> {

    protected RuleTypeEnum ruleType;

    public RuleTypeEnum getRuleType() {
        return ruleType;
    }

}

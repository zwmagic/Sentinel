package com.alibaba.csp.sentinel.dashboard.rule.apollo.aop;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.handler.DynamicRuleHandler;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.handler.DynamicRuleHandlerFactory;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wenming.zhang
 */
@Aspect
@Slf4j
public class SentinelApiClientAspect {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new NamedThreadFactory("sentinelApiClientAspect"));

    @Autowired
    private DynamicRuleHandlerFactory factory;

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchFlowRuleOfMachine(..))")
    public Object fetchFlowRuleOfMachine(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRules(pjp, RuleTypeEnum.FLOW);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setFlowRuleOfMachineAsync(..))")
    public Object setFlowRuleOfMachineAsync(final ProceedingJoinPoint pjp) {
        return publishRulesAsync(pjp, RuleTypeEnum.FLOW);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchDegradeRuleOfMachine(..))")
    public Object fetchDegradeRuleOfMachine(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRules(pjp, RuleTypeEnum.DEGRADE);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setDegradeRuleOfMachine(..))")
    public Object setDegradeRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleTypeEnum.DEGRADE);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchParamFlowRulesOfMachine(..))")
    public Object fetchParamFlowRulesOfMachine(final ProceedingJoinPoint pjp) {
        return fetchRulesAsync(pjp, RuleTypeEnum.PARAM_FLOW);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setParamFlowRuleOfMachine(..))")
    public Object setParamFlowRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRulesAsync(pjp, RuleTypeEnum.PARAM_FLOW);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchSystemRuleOfMachine(..))")
    public Object fetchSystemRuleOfMachine(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRules(pjp, RuleTypeEnum.SYSTEM);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setSystemRuleOfMachine(..))")
    public Object setSystemRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleTypeEnum.SYSTEM);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchAuthorityRulesOfMachine(..))")
    public Object fetchAuthorityRulesOfMachine(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRules(pjp, RuleTypeEnum.AUTHORITY);
    }

    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setAuthorityRuleOfMachine(..))")
    public Object setAuthorityRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleTypeEnum.AUTHORITY);
    }

    /**
     * 拉取网关流控规则配置
     */
    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchGatewayFlowRules(..))")
    public Object fetchGatewayFlowRules(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRulesAsync(pjp, RuleTypeEnum.GATEWAY_FLOW);
    }

    /**
     * 推送网关流控规则配置
     */
    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.modifyGatewayFlowRules(..))")
    public Object modifyGatewayFlowRules(final ProceedingJoinPoint pjp) throws Throwable {
        return publishRules(pjp, RuleTypeEnum.GATEWAY_FLOW);
    }

    /**
     * 拉取 gateway api 分组规则配置
     */
    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchApis(..))")
    public Object fetchApis(final ProceedingJoinPoint pjp) throws Throwable {
        return fetchRulesAsync(pjp, RuleTypeEnum.GATEWAY_API);
    }

    /**
     * 推送 gateway api 分组规则配置
     */
    @Around("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.modifyApis(..))")
    public Object modifyApis(final ProceedingJoinPoint pjp) throws Throwable {
        return publishRules(pjp, RuleTypeEnum.GATEWAY_API);
    }


    private Object fetchRules(ProceedingJoinPoint pjp, RuleTypeEnum ruleTypeEnum) throws Throwable {
        DynamicRuleHandler<?> handler = factory.getHandler(ruleTypeEnum);
        if (handler == null) {
            return pjp.proceed();
        }
        Object[] args = pjp.getArgs();
        String app = (String) args[0];
        return handler.getRules(app);
    }

    private CompletableFuture<Object> fetchRulesAsync(ProceedingJoinPoint pjp, RuleTypeEnum ruleTypeEnum) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return fetchRules(pjp, ruleTypeEnum);
            } catch (Throwable e) {
                throw new RuntimeException("fetch rules error: " + ruleTypeEnum.getName(), e);
            }
        }, EXECUTOR);
    }

    @SuppressWarnings("unchecked")
    private boolean publishRules(ProceedingJoinPoint pjp, RuleTypeEnum ruleTypeEnum) {
        DynamicRuleHandler<RuleEntity> handler = factory.getHandler(ruleTypeEnum);
        Object[] args = pjp.getArgs();
        String app = (String) args[0];
        List<RuleEntity> rules = (List<RuleEntity>) args[3];
        try {
            handler.publish(app, rules);
            return true;
        } catch (Exception e) {
            log.error("publish rules error", e);
            return false;
        }
    }

    private CompletableFuture<Void> publishRulesAsync(ProceedingJoinPoint pjp, RuleTypeEnum ruleTypeEnum) {
        return CompletableFuture.runAsync(() -> publishRules(pjp, ruleTypeEnum), EXECUTOR);
    }

}

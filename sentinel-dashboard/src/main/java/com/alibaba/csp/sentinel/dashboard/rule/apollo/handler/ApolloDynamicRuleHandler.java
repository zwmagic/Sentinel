package com.alibaba.csp.sentinel.dashboard.rule.apollo.handler;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.configuration.ApolloProperties;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.ApolloOpenApiClientProvider;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.Constants;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.support.RuleTypeEnum;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.client.exception.ApolloOpenApiException;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wenming.zhang
 */
@Slf4j
public class ApolloDynamicRuleHandler<T extends RuleEntity> extends DynamicRuleHandler<T> {

    private final ApolloProperties apolloProperties;
    private final ApolloOpenApiClientProvider openApiClientProvider;

    public ApolloDynamicRuleHandler(final RuleTypeEnum ruleType,
                                    final ApolloProperties apolloProperties,
                                    final ApolloOpenApiClientProvider openApiClientProvider) {
        super.ruleType = ruleType;
        this.apolloProperties = apolloProperties;
        this.openApiClientProvider = openApiClientProvider;
    }

    @Override
    public List<T> getRules(final String appName) {
        ApolloOpenApiClient apolloOpenApiClient = openApiClientProvider.get();
        String env = apolloProperties.getEnv();
        String cluster = apolloProperties.getCluster();
        String dataId = RuleConfigUtil.getDataId(appName, ruleType);

        String rules = "";
        try {
            OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(appName, env, cluster, Constants.APOLLO_NAMESPACE);
            rules = openNamespaceDTO
                    .getItems()
                    .stream()
                    .filter(p -> p.getKey().equals(dataId))
                    .map(OpenItemDTO::getValue)
                    .findFirst()
                    .orElse("");
        } catch (Exception e) {
            if (printLog(e)) {
                log.error("Failed to get rules from Apollo", e);
            }
        }
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        Converter<String, List<T>> decoder = RuleConfigUtil.getDecoder(ruleType.getClazz());
        return decoder.convert(rules);
    }

    private boolean printLog(Exception e) {
        if (e.getCause() == null) {
            return true;
        }
        if (!(e.getCause() instanceof ApolloOpenApiException)) {
            throw new RuntimeException(e.getCause().getMessage());
        }
        return ((ApolloOpenApiException) e.getCause()).getStatus() != 404;
    }

    @Override
    public void publish(final String appName, final List<T> rules) throws Exception {
        AssertUtil.notEmpty(appName, "appName cannot is empty");
        if (rules == null) {
            return;
        }

        createNamespace(appName);

        ApolloOpenApiClient apolloOpenApiClient = openApiClientProvider.get();
        String env = apolloProperties.getEnv();
        String clusterName = apolloProperties.getCluster();
        String dataId = RuleConfigUtil.getDataId(appName, ruleType);
        Converter<Object, String> encoder = RuleConfigUtil.getEncoder();
        String value = encoder.convert(rules);

        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(dataId);
        openItemDTO.setValue(value);
        openItemDTO.setComment("update from sentinel-dashboard");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getOperator());
        apolloOpenApiClient.createOrUpdateItem(appName, env, clusterName, Constants.APOLLO_NAMESPACE, openItemDTO);

        // Release configuration
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getOperator());
        namespaceReleaseDTO.setReleaseComment("Modify or add configurations");
        namespaceReleaseDTO.setReleaseTitle("Modify or add configurations");
        apolloOpenApiClient.publishNamespace(appName, env, clusterName, Constants.APOLLO_NAMESPACE, namespaceReleaseDTO);
        log.info("publish rule success - appName: {}, type: {}, value: {}", appName, ruleType.getName(), value);
    }

    private void createNamespace(String appName) {
        try {
            OpenAppNamespaceDTO dto = new OpenAppNamespaceDTO();
            dto.setAppId(appName);
            dto.setName(Constants.APOLLO_NAMESPACE);
            dto.setFormat("properties");
            dto.setComment("default " + Constants.APOLLO_NAMESPACE + " namespace");
            dto.setDataChangeCreatedBy(apolloProperties.getOperator());
            openApiClientProvider.get().createAppNamespace(dto);
        } catch (Exception ignore) {
        }
    }

}

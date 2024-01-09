package com.alibaba.csp.sentinel.dashboard.rule.apollo.support;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wenming.zhang
 */
public class RuleConfigUtil {

    private static final Converter<Object, String> ENCODER = JSON::toJSONString;
    private static final Map<Class<?>, Object> DECODER_MAP = new HashMap<>();

    private RuleConfigUtil() {
    }

    public static String getDataId(String appName, RuleTypeEnum ruleType) {
        return "rule." + ruleType.getName();
    }

    public static Converter<Object, String> getEncoder() {
        return ENCODER;
    }

    public static synchronized <T extends RuleEntity> Converter<String, List<T>> getDecoder(Class<T> clazz) {
        Object decoder = DECODER_MAP.computeIfAbsent(clazz,
                (Function<Class<?>, Converter<String, List<T>>>) targetClass -> source -> JSON.parseArray(source, clazz));
        return (Converter<String, List<T>>) decoder;
    }

}
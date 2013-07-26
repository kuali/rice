package org.kuali.rice.krad.util;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Prevents calls to "legacy" data framework services if the legacy framework
 * has been disabled
 */
public class LegacyDetectionAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (!LegacyUtils.isLegacyDataFrameworkEnabled()) {
            throw new IllegalStateException("Legacy data framework is disabled.  To use legacy KRAD data services, enable KNS via the KRADConfigurer or set the configuration parameter: " + KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);
        }
    }
}

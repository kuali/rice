/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

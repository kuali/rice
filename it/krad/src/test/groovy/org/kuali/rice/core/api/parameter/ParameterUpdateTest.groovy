package org.kuali.rice.core.api.parameter

import org.junit.Test
import org.junit.Assert;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator
import org.kuali.rice.core.api.parameter.Parameter
import org.kuali.rice.krad.util.KRADConstants
import org.kuali.rice.kew.api.KewApiConstants
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import org.kuali.rice.core.framework.parameter.ParameterService
import org.apache.commons.lang.time.StopWatch
import org.kuali.rice.core.api.cache.CacheService
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.springframework.cache.Cache
import javax.xml.namespace.QName
import org.kuali.test.KRADTestCase


class ParameterUpdateTest extends KRADTestCase {

    @Test
    void parameterCachingTest ( ){

        ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        Parameter parameter = parameterService.getParameter(KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND);


        String value = parameterService.getParameterValueAsString(KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND);
        assertNotNull("parameter should not be Null", parameter)

        //loop and get the same parameter to test caching
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        0.step(10000, 1) {
            parameter = parameterService.getParameter(KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND);
        }
        stopWatch.stop()
        LOG.info("loop time: " + stopWatch.getTime() + "ms");

        //CacheService cacheService = GlobalResourceLoader.getService(new QName("http://rice.kuali.org/core/v2_0", "coreCacheService"))

        //Cache cache = cacheService.getCache()

        //LOG.info(cache);
    }
}

package org.kuali.rice.kns;

import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.krad.test.KRADTestCase;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Test base class for legacy KNS Tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KNSTestCase extends KRADTestCase {

    @Override
    protected Lifecycle getLoadApplicationLifecycle() {
        List<String> resourceLocations = new ArrayList<String>();
        resourceLocations.add("classpath:KRADTestHarnessSpringBeans.xml");
        resourceLocations.add("classpath:KNSTestSpringBeans.xml");

        SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("KRADTestResourceLoader"),
                resourceLocations, null);
        springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());

        return springResourceLoader;
    }
}

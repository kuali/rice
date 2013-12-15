package org.kuali.rice.ksb.messaging;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.impl.cxf.interceptors.ServiceCallVersioningHelper;
import org.kuali.rice.ksb.messaging.remotedservices.BaseballCard;
import org.kuali.rice.ksb.messaging.remotedservices.BaseballCardCollectionService;
import org.kuali.rice.ksb.messaging.remotedservices.EchoService;
import org.kuali.rice.ksb.messaging.remotedservices.JaxWsEchoService;
import org.kuali.rice.ksb.messaging.remotedservices.ServiceCallInformationHolder;
import org.kuali.rice.ksb.test.KSBTestCase;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServiceCallVersioningTest extends KSBTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Config c = ConfigContext.getCurrentContextConfig();
        c.putProperty(Config.APPLICATION_NAME, "ServiceCallVersioningTest");
        c.putProperty(Config.APPLICATION_VERSION, "99.99-SNAPSHOT");
    }

    public boolean startClient1() {
        return true;
    }

    private String getClient1Port() {
        return ConfigContext.getCurrentContextConfig().getProperty("ksb.client1.port");
    }

    @Test
    public void testSimpleSOAPService() throws Exception {
        EchoService echoService = GlobalResourceLoader.getService(new QName("TestCl1", "soap-echoService"));
        echoService.captureHeaders();
        assertHeadersCaptured();
    }

    @Test
    public void testJaxWsSOAPService() {
        JaxWsEchoService jaxwsEchoService = GlobalResourceLoader.getService(new QName("TestCl1",
                "jaxwsEchoService"));
        jaxwsEchoService.captureHeaders();
        assertHeadersCaptured();
    }

    @Test
    public void testJaxRsService() {
        BaseballCardCollectionService service = GlobalResourceLoader.getService(new QName("test", "baseballCardCollectionService"));
        // invoke a method that stores the headers
        List<BaseballCard> allCards = service.getAll();
        Assert.assertNotNull(allCards);
        assertHeadersCaptured();
    }

    public void assertHeadersCaptured() {
        Map<String, List<String>> headers = ServiceCallInformationHolder.multiValues;
        Assert.assertNotNull(headers);
        System.out.println("HEADERS");
        System.out.println(headers);
        Assert.assertTrue(headers.get(ServiceCallVersioningHelper.KUALI_RICE_ENVIRONMENT_HEADER).contains("dev"));
        Assert.assertTrue(headers.get(ServiceCallVersioningHelper.KUALI_RICE_VERSION_HEADER).contains(
                ConfigContext.getCurrentContextConfig().getRiceVersion()));//any { it =~ /2\.0.*/ })
        Assert.assertTrue(headers.get(ServiceCallVersioningHelper.KUALI_APP_NAME_HEADER).contains(
                "ServiceCallVersioningTest"));
        Assert.assertTrue(headers.get(ServiceCallVersioningHelper.KUALI_APP_VERSION_HEADER).contains("99.99-SNAPSHOT"));
    }

}

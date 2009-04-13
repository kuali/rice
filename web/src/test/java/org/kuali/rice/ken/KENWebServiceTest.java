/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.core.GlobalNotificationServiceLocator;
import org.kuali.rice.kew.batch.KEWXmlDataLoaderLifecycle;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.server.test.ServerTestBase;
import org.kuali.rice.test.TransactionalLifecycle;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.web.HtmlUnitUtil;


/**
 * This is a description of what this class does - arh14 don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@PerTestUnitTestData({
  @UnitTestData(filename="file:impl/src/main/config/sql/KENBootstrap.sql", delimiter="/"),
  @UnitTestData(filename="file:ken/src/test/resources/org/kuali/rice/ken/test/DefaultTestData.sql", delimiter=";")
})
public class KENWebServiceTest extends ServerTestBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KENWebServiceTest.class);

	private static final String notificationMessageAsXml;
	private TransactionalLifecycle transactionalLifecycle;

    static {
        InputStream notificationXML = KENWebServiceTest.class.getResourceAsStream("webservice_notification.xml");
        try {
            notificationMessageAsXml = IOUtils.toString(notificationXML);
        } catch (IOException ioe) {
            throw new RuntimeException("Error loading webservice_notification.xml");
        }
        //System.err.println(notificationMessageAsXml);
    }

    @Override
	public void setUp() throws Exception {
		super.setUp();
		transactionalLifecycle = new TransactionalLifecycle();
		transactionalLifecycle.setTransactionManager(KNSServiceLocator.getTransactionManager());
		transactionalLifecycle.start();
	}

	@Override
	public void tearDown() throws Exception {
	    if ( (transactionalLifecycle != null) && (transactionalLifecycle.isStarted()) ) {
	        transactionalLifecycle.stop();
	    }
		super.tearDown();
	}

	@Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> list = super.getPerTestLifecycles();
        list.add(0, new KEWXmlDataLoaderLifecycle("file:ken/src/test/resources/org/kuali/rice/ken/test/DefaultTestData.xml"));
        list.add(0, new KEWXmlDataLoaderLifecycle("file:impl/src/main/config/xml/KENBootstrap.xml"));
        return list;
    }

    @Test
    public void wsdlIsAccessible() throws IOException {
        URL url = new URL(HtmlUnitUtil.BASE_URL + "/remoting/{TRAVEL}sendNotificationKewXmlSOAPService?wsdl");
        InputStream is = url.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        assertTrue(new String(baos.toByteArray()).contains("wsdl"));
    }

    @Test
    public void invokeService() throws IOException, Exception {
        QName serviceName = new QName("TRAVEL", "sendNotificationKewXmlSOAPService");
        KSBXMLService service = (KSBXMLService) GlobalResourceLoader.getService(serviceName);
        service.invoke(notificationMessageAsXml);
    }

    @Test
    public void invokeSOAPService() throws Exception {
        QName serviceName = new QName("TRAVEL", "sendNotificationKewXmlSOAPService");

        ConfigContext.getCurrentContextConfig().overrideProperty("bam.enabled", "true");
        KSBXMLService service = (KSBXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
        service.invoke(notificationMessageAsXml);

        Thread.sleep(40000);

        Collection<Notification> ns = GlobalNotificationServiceLocator.getInstance().getNotificationService().getNotificationsForRecipientByType("Simple", "TestUser2");
        assertEquals(1, ns.size());

        /*
        BAMService bamService = KSBServiceLocator.getBAMService();
        List<BAMTargetEntry> bamCalls = bamService.getCallsForService(serviceName);
        assertTrue("No service call recorded", bamCalls.size() > 0);
        boolean foundClientCall = false;
        boolean foundServiceCall = false;
        for (BAMTargetEntry bamEntry : bamCalls) {
            if (bamEntry.getServerInvocation()) {
                foundServiceCall = true;
            } else {
                foundClientCall = true;
            }
        }
        assertTrue("No client call recorded", foundClientCall);
        assertTrue("No service call recorded", foundServiceCall);
        assertEquals("Wrong number of calls recorded", 2, bamCalls.size());*/
    }
}

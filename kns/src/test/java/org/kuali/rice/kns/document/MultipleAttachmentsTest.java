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
package org.kuali.rice.kns.document;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.test.KNSTestCase;
import org.kuali.test.KNSWithTestSpringContext;

import edu.sampleu.travel.bo.AttachmentSample;
import edu.sampleu.travel.bo.MultiAttachmentSample;


/**
 * This class implements a unit test for a maintenance document
 * with multiple attachments. The need for this test is tracked 
 * under the JIRA ticket KULRICE-5144. 
 * 
 * @link https://jira.kuali.org/browse/KULRICE-5144
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@KNSWithTestSpringContext
public class MultipleAttachmentsTest extends KNSTestCase {

	MaintenanceDocument document;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        document = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument("AttachmentSampleMaintenanceDocument");
    }
	
    @Test 
    @UnitTestData(sqlStatements = {
            @UnitTestSql("delete from trv_attach_sample_t"),
            @UnitTestSql("delete from trv_multi_attach_sample_t")
        })
    public void test_MultipleAttachments() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, WorkflowException {

    	AttachmentSample as = new AttachmentSample();
    	as.setFileName("test.txt");
    	as.setId("2340");
    	as.setDescription("A test attachment");
    	as.setContentType("text/plain");
    	as.setAttachmentContent("This is a test".getBytes());
    	
    	MultiAttachmentSample child1 = new MultiAttachmentSample();
    	child1.setFileName("child1.txt");
    	child1.setDescription("This is the first child");
    	child1.setContentType("text/plain");
    	child1.setAttachmentContent("This is the content of the first child".getBytes());
    	as.getMultiAttachment().add(child1);
    	
    	MultiAttachmentSample child2 = new MultiAttachmentSample();
    	child2.setFileName("child2.txt");
    	child2.setDescription("This is the second child");
    	child2.setContentType("text/plain");
    	child2.setAttachmentContent("This is the content of the second child".getBytes());
    	as.getMultiAttachment().add(child2);
    	
    	MultiAttachmentSample child3 = new MultiAttachmentSample();
    	child3.setFileName("child3.txt");
    	child3.setDescription("This is the third child");
    	child3.setContentType("text/plain");
    	child3.setAttachmentContent("This is the content of the third child".getBytes());
    	as.getMultiAttachment().add(child3);
    	
    	
        document.getOldMaintainableObject().setBusinessObject(null);
        document.getOldMaintainableObject().setBoClass(as.getClass());
        document.getNewMaintainableObject().setBusinessObject(as);
        document.getNewMaintainableObject().setBoClass(as.getClass());
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription("Testing");

        Document routedDoc = KNSServiceLocator.getDocumentService().routeDocument(document, "Routing", null);
        
        MaintenanceDocument retrievedDoc = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getByDocumentHeaderId(routedDoc.getDocumentNumber());
        
        AttachmentSample ras = (AttachmentSample)retrievedDoc.getNewMaintainableObject().getBusinessObject();
        
        assertEquals("This is a test", new String(ras.getAttachmentContent()));
        assertEquals("text/plain", ras.getContentType());
        
        List<MultiAttachmentSample> multiAttachments = ras.getMultiAttachment();
        assertEquals(3, multiAttachments.size());
        
        int i=1;
        for (MultiAttachmentSample multiAttachment : multiAttachments) {
        	String expectedName = "child" + i + ".txt";
        	assertEquals(expectedName, multiAttachment.getFileName());
        	assertEquals("text/plain", multiAttachment.getContentType());
        	i++;
        }
    }
	
}

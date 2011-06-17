/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.docmentlink;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.kew.dto.DocumentLinkDTO;

import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.util.List;

import static org.junit.Assert.*;

/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class DocumentLinkTest extends KEWTestCase{

	private static final Logger LOG = Logger.getLogger(DocumentLinkTest.class);

	@Test public void testAddLinkBTW2DocsSucess() throws Exception {
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("6000");
		testDocLinkVO.setDestDocId("5000");

		DocumentLinkDTO link2 = doc.getLinkedDocument(testDocLinkVO);

		assertEquals(link1.getOrgnDocId(), link2.getDestDocId());
		assertEquals(link2.getOrgnDocId(), link1.getDestDocId());

	}

	@Test public void testAddDuplicatedLinkBTW2DocsFailure() throws Exception {
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");

		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links = doc.getLinkedDocumentsByDocId("5000");

		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId("5000");

		assertEquals(links.size(), links2.size());

	}

	@Test public void testAddIncomplelteLinkBTW2DocsFailure() throws Exception{

		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();

		try{
			doc.addLinkedDocument(testDocLinkVO);
			assertFalse(true);
		}
		catch(WorkflowRuntimeException e){
			assertTrue(e.getMessage().contains("doc id is null"));
		}

		try{

			testDocLinkVO.setOrgnDocId("6000");
			doc.addLinkedDocument(testDocLinkVO);
			assertFalse(true);
		}
		catch(WorkflowRuntimeException e){
			assertTrue(e.getMessage().contains("doc id is null"));
		}

	}

	@Test public void testGetLinkBTW2DocsSucess() throws Exception{

		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);

		assertNotNull(link1);
		assertEquals(testDocLinkVO.getOrgnDocId(), link1.getOrgnDocId());
		assertEquals(testDocLinkVO.getDestDocId(), link1.getDestDocId());

	}

	@Test public void testGetLinkBTW2DocsFailure() throws Exception{

		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5001");

		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);

		assertEquals(null, link1);

	}

	@Test public void testGetAllLinksFromOrgnDocSucess() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");


		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5009");
		testDocLinkVO.setDestDocId("6009");
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6003");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6004");

		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId("5000");

		assertEquals(3, links2.size());

	}

	@Test public void testGetAllLinksFromOrgnDocFailure()throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5009");
		testDocLinkVO.setDestDocId("6009");
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6003");
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId("8000");

		assertEquals(0, links2.size());

	}

	@Test public void testRemoveLinkBTW2DocsSucess() throws Exception{

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links1 = doc.getLinkedDocumentsByDocId("5000");

		assertEquals(1, links1.size());

		List<DocumentLinkDTO> links0 = doc.getLinkedDocumentsByDocId("6000");

		assertEquals(1, links0.size());

		doc.removeLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId("5000");

		assertEquals(0, links2.size());
	}

	@Test public void testRemoveAllLinksFromOrgnDocSucess() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6000");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5000");
		testDocLinkVO.setDestDocId("6002");
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links01 = doc.getLinkedDocumentsByDocId("5000");
		List<DocumentLinkDTO> links02 = doc.getLinkedDocumentsByDocId("6000");
		List<DocumentLinkDTO> links03 = doc.getLinkedDocumentsByDocId("6002");

		assertEquals(2, links01.size());
		assertEquals(1, links02.size());
		assertEquals(1, links03.size());


		doc.removeLinkedDocuments("5000");

		links01 = doc.getLinkedDocumentsByDocId("5000");
		links02 = doc.getLinkedDocumentsByDocId("6000");
		links03 = doc.getLinkedDocumentsByDocId("6002");

		assertEquals(0, links01.size());
		assertEquals(0, links02.size());
		assertEquals(0, links03.size());

	}

	@Test public void testDocLinktoItself() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();

		try{
			//DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
			WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

			//Test add link	
			testDocLinkVO.setOrgnDocId("5000");
			testDocLinkVO.setDestDocId("5000");
			doc.addLinkedDocument(testDocLinkVO);
			fail();
		}
		catch(WorkflowRuntimeException ex){
			assertTrue(ex.getMessage().contains("no self link"));
			
		}
	}

	//not a real test case....
	@Test public void testDocLinkClient() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId("5009");
		testDocLinkVO.setDestDocId("6008");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5009");
		testDocLinkVO.setDestDocId("6009");
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId("5002");
		testDocLinkVO.setDestDocId("6002");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId("5003");
		testDocLinkVO.setDestDocId("6003");
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links = doc.getLinkedDocumentsByDocId("5009");

		for(DocumentLinkDTO link : links)
			LOG.info("******************************\t link btw:\t" + link.getOrgnDocId()+ "\t" + link.getDestDocId());

		//doc.removeLinkedDocument(testDocLinkVO);
		//	
	}

	@Test public void testAddDocLinkWithLinkID() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId("5009");
		testDocLinkVO.setDestDocId("6009");
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setLinbkId(Long.valueOf(2106));
		testDocLinkVO.setOrgnDocId("5010");
		testDocLinkVO.setDestDocId("6010");
		doc.addLinkedDocument(testDocLinkVO);

		//add non-null link id

		//add link id < the next biggest value

		//add link id = on of other link id
		//get the one just insserted, repuse the id

		testDocLinkVO.setLinbkId(null);
		testDocLinkVO.setOrgnDocId("5011");
		testDocLinkVO.setDestDocId("6011");
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links = doc.getLinkedDocumentsByDocId("5009");

		for(DocumentLinkDTO link : links)
			LOG.info("******************************\t link btw:\t" + link.getOrgnDocId()+ "\t" + link.getDestDocId());

		//doc.removeLinkedDocument(testDocLinkVO);
		//	
	}
}

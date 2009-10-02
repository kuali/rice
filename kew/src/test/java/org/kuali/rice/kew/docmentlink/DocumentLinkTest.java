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
package org.kuali.rice.kew.docmentlink;

import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.DocumentLinkDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.test.KEWTestCase;


/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentLinkTest extends KEWTestCase{

	private static final Logger LOG = Logger.getLogger(DocumentLinkTest.class);
	
	@Test public void testAddLinkBTW2DocsSucess() throws Exception {
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);
		
		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);
			
		testDocLinkVO.setOrgnDocId(Long.valueOf(6000));
		testDocLinkVO.setDestDocId(Long.valueOf(5000));
		
		DocumentLinkDTO link2 = doc.getLinkedDocument(testDocLinkVO);
		
		assertEquals(link1.getOrgnDocId(), link2.getDestDocId());
		assertEquals(link2.getOrgnDocId(), link1.getDestDocId());

	}
	
	@Test public void testAddDuplicatedLinkBTW2DocsFailure() throws Exception {
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		
		assertEquals(links.size(), links2.size());
		
	}
	
	@Test public void testAddIncomplelteLinkBTW2DocsFailure() throws Exception{
		
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		try{
			doc.addLinkedDocument(testDocLinkVO);
			
			assertFalse(true);
		}
		catch(WorkflowException e){
			assertSame("passed", "doc id is null", e.getMessage());
		}
		
		try{
			
			testDocLinkVO.setOrgnDocId(Long.valueOf(6000));
			doc.addLinkedDocument(testDocLinkVO);
			assertFalse(true);
		}
		catch(WorkflowException e){
			assertSame("passed", "doc id is null", e.getMessage());
		}
		
	}
	
	@Test public void testGetLinkBTW2DocsSucess() throws Exception{
		
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);
		
		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);
		
		assertNotNull(link1);
		assertEquals(testDocLinkVO.getOrgnDocId(), link1.getOrgnDocId());
		assertEquals(testDocLinkVO.getDestDocId(), link1.getDestDocId());
	
	}
	
	@Test public void testGetLinkBTW2DocsFailure() throws Exception{
		
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);
		
		testDocLinkVO.setOrgnDocId(Long.valueOf(5001));
		
		DocumentLinkDTO link1 = doc.getLinkedDocument(testDocLinkVO);
		
		assertEquals(null, link1);
	
	}
	
	@Test public void testGetAllLinksFromOrgnDocSucess() throws Exception {
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

		
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId(Long.valueOf(5009));
		testDocLinkVO.setDestDocId(Long.valueOf(6009));
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6003));
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6004));
		
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		
		assertEquals(3, links2.size());
		
	}
	
	@Test public void testGetAllLinksFromOrgnDocFailure()throws Exception {
			
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

		
		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId(Long.valueOf(5009));
		testDocLinkVO.setDestDocId(Long.valueOf(6009));
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6003));
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId(Long.valueOf(8000));
		
		assertEquals(0, links2.size());
		
	}
	
	@Test public void testRemoveLinkBTW2DocsSucess() throws Exception{
	
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links1 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		
		assertEquals(1, links1.size());
		
		List<DocumentLinkDTO> links0 = doc.getLinkedDocumentsByDocId(Long.valueOf(6000));
		
		assertEquals(1, links0.size());
		
		doc.removeLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links2 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		
		assertEquals(0, links2.size());
	}
	
	@Test public void testRemoveAllLinksFromOrgnDocSucess() throws Exception {
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6000));
		doc.addLinkedDocument(testDocLinkVO);
		
		testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
		testDocLinkVO.setDestDocId(Long.valueOf(6002));
		doc.addLinkedDocument(testDocLinkVO);
		
		List<DocumentLinkDTO> links01 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		List<DocumentLinkDTO> links02 = doc.getLinkedDocumentsByDocId(Long.valueOf(6000));
		List<DocumentLinkDTO> links03 = doc.getLinkedDocumentsByDocId(Long.valueOf(6002));
		
		assertEquals(2, links01.size());
		assertEquals(1, links02.size());
		assertEquals(1, links03.size());
		
		
		doc.removeLinkedDocuments(Long.valueOf(5000));
		
		links01 = doc.getLinkedDocumentsByDocId(Long.valueOf(5000));
		links02 = doc.getLinkedDocumentsByDocId(Long.valueOf(6000));
		links03 = doc.getLinkedDocumentsByDocId(Long.valueOf(6002));
		
		assertEquals(0, links01.size());
		assertEquals(0, links02.size());
		assertEquals(0, links03.size());
		
	}
	
	@Test public void testDocLinktoItself() {
		
		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		
		try{
			//DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
			WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

			//Test add link	
			testDocLinkVO.setOrgnDocId(Long.valueOf(5000));
			testDocLinkVO.setDestDocId(Long.valueOf(5000));
			doc.addLinkedDocument(testDocLinkVO);
			
			assertFalse(true);
		}
		catch(WorkflowException wfle){
			LOG.info("******************************\t link btw:\t" + testDocLinkVO.getOrgnDocId()+ "\t" + testDocLinkVO.getDestDocId());		
			assertSame("passed", "no self link", wfle.getMessage());
		}
	}
	
	//not a real test case....
	@Test public void testDocLinkClient() throws Exception {

		DocumentLinkDTO testDocLinkVO = new DocumentLinkDTO();
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "TestDocumentType");

		//Test add link	
		testDocLinkVO.setOrgnDocId(Long.valueOf(5009));
		testDocLinkVO.setDestDocId(Long.valueOf(6008));
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId(Long.valueOf(5009));
		testDocLinkVO.setDestDocId(Long.valueOf(6009));
		doc.addLinkedDocument(testDocLinkVO);
		//		
		testDocLinkVO.setOrgnDocId(Long.valueOf(5002));
		testDocLinkVO.setDestDocId(Long.valueOf(6002));
		doc.addLinkedDocument(testDocLinkVO);

		testDocLinkVO.setOrgnDocId(Long.valueOf(5003));
		testDocLinkVO.setDestDocId(Long.valueOf(6003));
		doc.addLinkedDocument(testDocLinkVO);

		List<DocumentLinkDTO> links = doc.getLinkedDocumentsByDocId(Long.valueOf(5009));

		for(DocumentLinkDTO link : links)
			LOG.info("******************************\t link btw:\t" + link.getOrgnDocId()+ "\t" + link.getDestDocId());

		//doc.removeLinkedDocument(testDocLinkVO);
		//	
	}
}

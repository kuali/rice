/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.bo;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SessionDocumentTest {

	SessionDocument dummySessionDocument;
	
	@Before
	public void setUp() throws Exception {
		dummySessionDocument = new SessionDocument();
	}

	/**
	 * This method ...
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dummySessionDocument = null;
	}
	
	@Test
	public void testSerializedDocumentForm(){
		
		byte[] dummyByte = "dummy".getBytes();
		dummySessionDocument.setSerializedDocumentForm(dummyByte);
		assertEquals("Testing SerializedDocumentForm in SessionDocumentService","dummy", new String(dummySessionDocument.getSerializedDocumentForm()));
	}
	
	@Test
	public void testSessionId(){
		dummySessionDocument.setSessionId("dummySeesionID");
		assertEquals("Testing SessionId in SessionDocumentService","dummySeesionID",dummySessionDocument.getSessionId());
	}
	

	@Test
	public void testLastUpdatedDate(){
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());
		dummySessionDocument.setLastUpdatedDate(currentTimestamp);
		assertEquals("Testing LastUpdatedDate in SessionDocumentService",currentTimestamp,dummySessionDocument.getLastUpdatedDate());
	}
	
	@Test
	public void testDocumentNumber(){
		dummySessionDocument.setDocumentNumber("dummyDocumentNumber");
		assertEquals("Testing DocumentNumber in SessionDocumentService","dummyDocumentNumber",dummySessionDocument.getDocumentNumber());
	}
	

	@Test
	public void testPrincipalId(){
		dummySessionDocument.setPrincipalId("dummyPrincipalId");
		assertEquals("Testing PrincipalId in SessionDocumentService","dummyPrincipalId",dummySessionDocument.getPrincipalId());
	}
	
	@Test
	public void testIpAddress(){
		dummySessionDocument.setIpAddress("dummyIpAddress");
		assertEquals("Testing IpAddress in SessionDocumentService","dummyIpAddress",dummySessionDocument.getIpAddress());
	}
	
	@Test
	public void testEncrypted(){
		dummySessionDocument.setEncrypted(true);
		assertEquals("Testing Encrypted in SessionDocumentService",true,dummySessionDocument.isEncrypted());
	}
}

/*
 * Copyright 2007-2010 The Kuali Foundation
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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;

import java.sql.Date;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeReferenceDummyTest {

	AttributeReferenceDummy dummyARD;
	
	@Before
	public void setUp() throws Exception {
		dummyARD = new AttributeReferenceDummy();
	}

	/**
	 * This method ...
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dummyARD = null;
	}
	@Test
	public void testCreateDate(){	
		Date currentDate = java.sql.Date.valueOf( "2010-08-24" );
		dummyARD.setCreateDate(currentDate);
		assertEquals("Testing CreateDate in AttributeReferenceDummy",currentDate,dummyARD.getCreateDate());
	}
	
	@Test
	public void testInitiatorNetworkId(){	
		String initiatorNetworkId = "InitiatorNetworkId";
		dummyARD.setInitiatorNetworkId(initiatorNetworkId);
		assertEquals("Testing InitiatorNetworkId in AttributeReferenceDummy",initiatorNetworkId,dummyARD.getInitiatorNetworkId());
	}
	
	@Test
	public void testPercent(){	
		KualiPercent dummyPercent = new KualiPercent(30);
		dummyARD.setPercent(dummyPercent);
		assertEquals("Testing Percent in AttributeReferenceDummy",dummyPercent,dummyARD.getPercent());
	}
	
	@Test
	public void testGenericSystemId(){	
		String genericSystemId = "GenericSystemId";
		dummyARD.setGenericSystemId(genericSystemId);
		assertEquals("Testing GenericSystemId in AttributeReferenceDummy",genericSystemId,dummyARD.getGenericSystemId());
	}
	
	@Test
	public void testOneDigitTextCode(){	
		String oneDigitTextCode = "OneDigitTextCode";
		dummyARD.setOneDigitTextCode(oneDigitTextCode);
		assertEquals("Testing OneDigitTextCode in AttributeReferenceDummy",oneDigitTextCode,dummyARD.getOneDigitTextCode());
	}
	
	@Test
	public void testGenericTimestamp(){	
		Timestamp genericTimestamp = Timestamp.valueOf("2008-08-24 13:01:10");
		dummyARD.setGenericTimestamp(genericTimestamp);
		assertEquals("Testing GenericTimestamp in AttributeReferenceDummy",genericTimestamp,dummyARD.getGenericTimestamp());
	}
	
	@Test
	public void testTwoDigitTextCode(){	
		String twoDigitTextCode = "TwoDigitTextCode";
		dummyARD.setTwoDigitTextCode(twoDigitTextCode);
		assertEquals("Testing twoDigitTextCode in AttributeReferenceDummy",twoDigitTextCode,dummyARD.getTwoDigitTextCode());
	}
	
	@Test
	public void testGenericDate(){	
		Date genericDate = java.sql.Date.valueOf( "2010-08-24" );
		dummyARD.setGenericDate(genericDate);
		assertEquals("Testing GenericDate in AttributeReferenceDummy",genericDate,dummyARD.getGenericDate());
	}
	
	@Test
	public void testGenericBoolean(){	
		Boolean genericBoolean = true;
		dummyARD.setGenericBoolean(genericBoolean);
		assertEquals("Testing GenericBoolean in AttributeReferenceDummy",genericBoolean,dummyARD.isGenericBoolean());
	}
	
	@Test
	public void testActiveIndicator(){	
		Boolean activeIndicator = true;
		dummyARD.setActiveIndicator(activeIndicator);
		assertEquals("Testing ActiveIndicator in AttributeReferenceDummy",activeIndicator,dummyARD.isActiveIndicator());
	}
	
	@Test
	public void testGenericAmount(){	
		KualiDecimal genericAmount = new KualiDecimal(10.12);
		dummyARD.setGenericAmount(genericAmount);
		assertEquals("Testing GenericAmount in AttributeReferenceDummy",genericAmount,dummyARD.getGenericAmount());
	}
	
	@Test
	public void testGenericBigText(){	
		String genericBigText = "GenericBigText";
		dummyARD.setGenericBigText(genericBigText);
		assertEquals("Testing GenericBigText in AttributeReferenceDummy",genericBigText,dummyARD.getGenericBigText());
	}
	
	@Test
	public void testEmailAddress(){	
		String emailAddress = "EmailAddress";
		dummyARD.setEmailAddress(emailAddress);
		assertEquals("Testing EmailAddress in AttributeReferenceDummy",emailAddress,dummyARD.getEmailAddress());
	}
	
	@Test
	public void testNewCollectionRecord(){	
		Boolean newCollectionRecord = true;
		dummyARD.setNewCollectionRecord(newCollectionRecord);
		assertEquals("Testing NewCollectionRecord in AttributeReferenceDummy",newCollectionRecord,dummyARD.isNewCollectionRecord());
	}
	
	@Test
	public void testWorkflowDocumentStatus(){	
		String workflowDocumentStatus = "WorkflowDocumentStatus";
		dummyARD.setWorkflowDocumentStatus(workflowDocumentStatus);
		assertEquals("Testing WorkflowDocumentStatus in AttributeReferenceDummy",workflowDocumentStatus,dummyARD.getWorkflowDocumentStatus());
	}
}

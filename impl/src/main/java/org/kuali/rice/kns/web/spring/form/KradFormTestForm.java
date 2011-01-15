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
package org.kuali.rice.kns.web.spring.form;

import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

import edu.sampleu.travel.bo.TravelAccount;

/**
 * Test class for KRAD Form
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradFormTestForm extends KualiTransactionalDocumentFormBase {

	private KualiDecimal kDecimal;
	private boolean checkTest;
	private String testText;
	private String selectTest;
	private String radioTest;
	private String startDate;

	private TravelAccount travelAccount1;

	public KradFormTestForm() {
		travelAccount1 = new TravelAccount();
	}

	public KualiDecimal getkDecimal() {
    	return this.kDecimal;
    }

	public void setkDecimal(KualiDecimal kDecimal) {
    	this.kDecimal = kDecimal;
    }

	public boolean isCheckTest() {
    	return this.checkTest;
    }

	public void setCheckTest(boolean checkTest) {
    	this.checkTest = checkTest;
    }

	public String getTestText() {
    	return this.testText;
    }

	public void setTestText(String testText) {
    	this.testText = testText;
    }

	public String getSelectTest() {
    	return this.selectTest;
    }

	public void setSelectTest(String selectTest) {
    	this.selectTest = selectTest;
    }

	public String getRadioTest() {
    	return this.radioTest;
    }

	public void setRadioTest(String radioTest) {
    	this.radioTest = radioTest;
    }

	public String getStartDate() {
    	return this.startDate;
    }

	public void setStartDate(String startDate) {
    	this.startDate = startDate;
    }

	public TravelAccount getTravelAccount1() {
    	return this.travelAccount1;
    }

	public void setTravelAccount1(TravelAccount travelAccount1) {
    	this.travelAccount1 = travelAccount1;
    }

}

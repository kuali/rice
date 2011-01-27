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

import java.util.Date;

import edu.sampleu.travel.bo.TravelAccount;

/**
 * Form for Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UITestForm extends UifFormBase {
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;
	private Date field6;
	private int field7;
	private boolean field8;

	private boolean field9;
	private boolean field10;
	private boolean field11;
	private boolean field12;

	private TravelAccount travelAccount1;

	public UITestForm() {
		travelAccount1 = new TravelAccount();
	}

	public String getField1() {
		return this.field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return this.field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return this.field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return this.field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return this.field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public Date getField6() {
		return this.field6;
	}

	public void setField6(Date field6) {
		this.field6 = field6;
	}

	public int getField7() {
		return this.field7;
	}

	public void setField7(int field7) {
		this.field7 = field7;
	}

	public boolean isField8() {
		return this.field8;
	}

	public void setField8(boolean field8) {
		this.field8 = field8;
	}

	public TravelAccount getTravelAccount1() {
		return this.travelAccount1;
	}

	public void setTravelAccount1(TravelAccount travelAccount1) {
		this.travelAccount1 = travelAccount1;
	}

	public boolean isField9() {
		return this.field9;
	}

	public void setField9(boolean field9) {
		this.field9 = field9;
	}

	public boolean isField10() {
		return this.field10;
	}

	public void setField10(boolean field10) {
		this.field10 = field10;
	}

	public boolean isField11() {
		return this.field11;
	}

	public void setField11(boolean field11) {
		this.field11 = field11;
	}

	public boolean isField12() {
		return this.field12;
	}

	public void setField12(boolean field12) {
		this.field12 = field12;
	}

}

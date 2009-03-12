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
package org.kuali.rice.kns.datadictionary;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class LookupFieldDefinition extends FieldDefinition {

	protected boolean hidden 	= false;
	protected boolean readOnly 	= false;


	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * @param hidden
     *  If the ControlDefinition.isHidden == true then a corresponding LookupDefinition would
     *  automatically be removed from the search criteria.  In some cases you might want the
     *  hidden field to be used as a search criteria.  For example, in PersonImpl.xml a client
     *  might want to have the campus code hidden and preset to Bloomington.  So when the search
     *  is run, only people from the bloomington campus are returned.
     *
     *   So, if you want to have a hidden search criteria, set this variable to true. Defaults to
     *   false.
     */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


}

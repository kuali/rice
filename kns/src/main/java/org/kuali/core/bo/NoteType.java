/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.core.bo;

import java.util.LinkedHashMap;


/**
 * 
 */
public class NoteType extends PersistableBusinessObjectBase {

	private String noteTypeCode;
	private String noteTypeDescription;
	private boolean noteTypeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public NoteType() {

	}

	/**
	 * Gets the noteTypeCode attribute.
	 * 
	 * @return Returns the noteTypeCode
	 * 
	 */
	public String getNoteTypeCode() { 
		return noteTypeCode;
	}

	/**
	 * Sets the noteTypeCode attribute.
	 * 
	 * @param noteTypeCode The noteTypeCode to set.
	 * 
	 */
	public void setNoteTypeCode(String noteTypeCode) {
		this.noteTypeCode = noteTypeCode;
	}


	/**
	 * Gets the noteTypeDescription attribute.
	 * 
	 * @return Returns the noteTypeDescription
	 * 
	 */
	public String getNoteTypeDescription() { 
		return noteTypeDescription;
	}

	/**
	 * Sets the noteTypeDescription attribute.
	 * 
	 * @param noteTypeDescription The noteTypeDescription to set.
	 * 
	 */
	public void setNoteTypeDescription(String noteTypeDescription) {
		this.noteTypeDescription = noteTypeDescription;
	}


	/**
	 * Gets the noteTypeActiveIndicator attribute.
	 * 
	 * @return Returns the noteTypeActiveIndicator
	 * 
	 */
	public boolean isNoteTypeActiveIndicator() { 
		return noteTypeActiveIndicator;
	}
	

	/**
	 * Sets the noteTypeActiveIndicator attribute.
	 * 
	 * @param noteTypeActiveIndicator The noteTypeActiveIndicator to set.
	 * 
	 */
	public void setNoteTypeActiveIndicator(boolean noteTypeActiveIndicator) {
		this.noteTypeActiveIndicator = noteTypeActiveIndicator;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("noteTypeCode", this.noteTypeCode);
	    return m;
    }
}

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
package org.kuali.rice.kew.dto;

import java.io.Serializable;

import org.kuali.rice.kew.exception.WorkflowException;

/**
 * Transport object for a documentLink
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentLinkDTO implements Serializable {

	private static final long serialVersionUID = 4454393424187842416L;

	private Long docLinkId;
	private String orgnDocId;
	private String destDocId;
	/**
	 * @return the linbkId
	 */
	public Long getLinbkId() {
		return this.docLinkId;
	}
	/**
	 * @param linbkId the linbkId to set
	 */
	public void setLinbkId(Long linkId) {
		this.docLinkId = linkId;
	}
	/**
	 * @return the orgnDocId
	 */
	public String getOrgnDocId() {
		return this.orgnDocId;
	}
	/**
	 * @param orgnDocId the orgnDocId to set
	 */
	public void setOrgnDocId(String orgnDocId) {
		this.orgnDocId = orgnDocId;
	}
	/**
	 * @return the destDocId
	 */
	public String getDestDocId() {
		return this.destDocId;
	}
	/**
	 * @param destDocId the destDocId to set
	 */
	public void setDestDocId(String destDocId) {
		this.destDocId = destDocId;
	}

	public static boolean checkDocLink(DocumentLinkDTO docLinkVO) throws WorkflowException{
		if (docLinkVO == null) 
			throw new WorkflowException("doc link is null");

		if(docLinkVO.getOrgnDocId() == null || docLinkVO.getDestDocId() == null)
			throw new WorkflowException("doc id is null");


		if(docLinkVO.getOrgnDocId().equals(docLinkVO.getDestDocId()))
			throw new WorkflowException("no self link");
		
		return true;
	}
}


/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.docsearch.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.iu.uis.eden.doctype.DocumentType;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocSearchTransformationTag extends TagSupport {

	private String docTypeName;

	@Override
	public int doEndTag() throws JspException {
		try {
			// as an example of a default style, look at edoclite/edlstyle.xml
			DocumentType docType = null;// TODO
			// get stylesheet and get DOM and pass those in as first 2 params to transform
			JspWriter writer = pageContext.getOut();
			// TODO delyea - fix here
			//KEWServiceLocator.getStyleService();
			return super.doEndTag();
		} catch (Exception e) {
			throw new JspException(e);
		}
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

}

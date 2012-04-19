/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.element;


/**
 * Content element that encloses an iframe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Iframe extends ContentElementBase {
	private static final long serialVersionUID = 5797473302619055088L;

	private String source;
	private String height;
	private String frameborder;

	public Iframe() {
		super();
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getFrameborder() {
		return this.frameborder;
	}

	public void setFrameborder(String frameborder) {
		this.frameborder = frameborder;
	}

}

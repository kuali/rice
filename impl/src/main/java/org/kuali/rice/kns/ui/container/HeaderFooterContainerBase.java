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
package org.kuali.rice.kns.ui.container;

import org.apache.commons.lang.StringUtils;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class HeaderFooterContainerBase extends ContainerBase {
	private boolean renderHeader;

	private Header header;
	private Footer footer;

	public HeaderFooterContainerBase() {
		renderHeader = true;
	}

	@Override
	public void initialize() {
		super.initialize();

		// if header title not given, use the container title
		if (header != null && StringUtils.isBlank(header.getTitle())) {
			header.setTitle(this.getTitle());
		}
	}

	public Header getHeader() {
		return this.header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Footer getFooter() {
		return this.footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	public boolean isRenderHeader() {
		return this.renderHeader;
	}

	public void setRenderHeader(boolean renderHeader) {
		this.renderHeader = renderHeader;
	}

}

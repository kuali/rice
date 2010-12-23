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
public class PanelSection extends Section {
	private String wrappedSectionTemplate;

	private boolean defaultOpen;

	private Header panelHeader;

	public PanelSection() {
		defaultOpen = true;
	}

	@Override
	public void initialize() {
		super.initialize();

		// if panel title not given, use the section title on the panel
		if (StringUtils.isBlank(panelHeader.getTitle())) {
			panelHeader.setTitle(this.getTitle());
		}
	}

	public boolean isDefaultOpen() {
		return this.defaultOpen;
	}

	public void setDefaultOpen(boolean defaultOpen) {
		this.defaultOpen = defaultOpen;
	}

	public Header getPanelHeader() {
		return this.panelHeader;
	}

	public void setPanelHeader(Header panelHeader) {
		this.panelHeader = panelHeader;
	}

	public String getWrappedSectionTemplate() {
		return this.wrappedSectionTemplate;
	}

	public void setWrappedSectionTemplate(String wrappedSectionTemplate) {
		this.wrappedSectionTemplate = wrappedSectionTemplate;
	}

}

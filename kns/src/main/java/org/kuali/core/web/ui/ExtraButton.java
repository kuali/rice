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
package org.kuali.core.web.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.rice.KNSServiceLocator;

/**
 * Represents an extra button that may appear on the lookups or bottom of a
 * document page.
 */
public class ExtraButton {
	private String extraButtonSource = "";

	private String extraButtonAltText = "";

	private String extraButtonParams = "";

	private String extraButtonProperty = "";

	public String getExtraButtonAltText() {
		return extraButtonAltText;
	}

	public void setExtraButtonAltText(String extraButtonAltText) {
		this.extraButtonAltText = extraButtonAltText;
	}

	public String getExtraButtonParams() {
		return extraButtonParams;
	}

	public void setExtraButtonParams(String extraButtonParams) {
		this.extraButtonParams = extraButtonParams;
	}

	public String getExtraButtonProperty() {
		return extraButtonProperty;
	}

	public void setExtraButtonProperty(String extraButtonProperty) {
		this.extraButtonProperty = extraButtonProperty;
	}

	public String getExtraButtonSource() {
		return extraButtonSource;
	}

	public void setExtraButtonSource(String extraButtonSource) {
		if (StringUtils.isNotBlank(extraButtonSource)) {
			this.extraButtonSource = extraButtonSource
					.replace(
							"${kr.externalizable.images.url}",
							KNSServiceLocator
									.getKualiConfigurationService()
									.getPropertyString(
											Constants.EXTERNALIZABLE_IMAGES_URL_KEY))
					.replace(
							"${externalizable.images.url}",
							KNSServiceLocator
									.getKualiConfigurationService()
									.getPropertyString(
											Constants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY));
		}
	}

}

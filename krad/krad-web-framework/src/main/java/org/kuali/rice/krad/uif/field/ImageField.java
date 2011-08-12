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
package org.kuali.rice.krad.uif.field;

/**
 * Field that encloses an image element
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImageField extends FieldBase {
	private static final long serialVersionUID = -7994212503770623408L;

	private String source;
	private String altText;
	private String height;
    private String width;

	public ImageField() {
		super();
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAltText() {
		return this.altText;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }
}

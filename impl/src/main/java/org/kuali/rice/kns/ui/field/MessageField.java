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
package org.kuali.rice.kns.ui.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.ui.UIFConstants.MessageType;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageField extends FieldBase {
	private String messageText;
	private String messageType;

	public MessageField() {

	}

	/**
	 * Override to render only if the message text has been given
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#isRender()
	 */
	@Override
	public boolean isRender() {
		boolean render = super.isRender();

		if (render) {
			render = StringUtils.isNotBlank(messageText);
		}

		return render;
	}

	public MessageField(String messageType) {
		this.messageType = messageType;
	}

	public MessageField(String messageText, String messageType) {
		this.messageText = messageText;
		this.messageType = messageType;
	}

	public String getMessageText() {
		return this.messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getMessageType() {
		return this.messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}

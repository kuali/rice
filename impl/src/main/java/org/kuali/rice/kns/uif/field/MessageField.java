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
package org.kuali.rice.kns.uif.field;

import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates a text message to be displayed
 * 
 * <p>
 * The <code>MessageField</code> is used to display static text in the user
 * interface. The message type can be used to group similar messages for styling
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageField extends FieldBase {
	private static final long serialVersionUID = 4090058533452450395L;

	private String messageText;
	private String messageType;

	public MessageField() {

	}

	/**
	 * Override to render only if the message text has been given
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#isRender()
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

/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.field;

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
		super();
	}

	/**
	 * Override to render only if the message text has been given or there is a conditional expression on the
     * message text
	 *
	 * @see org.kuali.rice.krad.uif.component.ComponentBase#isRender()
	 */
	@Override
	public boolean isRender() {
		boolean render = super.isRender();

        if (render) {
            render = getPropertyExpressions().containsKey("messageText") || (StringUtils.isNotBlank(messageText)
                    && !StringUtils.equals(messageText, "&nbsp;"));
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

	/**
	 * Text that makes up the message that will be displayed
	 * 
	 * @return String message text
	 */
	public String getMessageText() {
		return this.messageText;
	}

	/**
	 * Setter for the message text
	 * 
	 * @param messageText
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	/**
	 * Type of the field's message, used to suffix the message fields id
	 * 
	 * <p>
	 * Messages that have similar intent can be grouped by this type string. For
	 * messages of the same type, their id will contain the same suffix which
	 * can be used for scripting to apply additional styling or behavior to that
	 * groups of messages (for example show/hide)
	 * </p>
	 * 
	 * @return String message type
	 */
	public String getMessageType() {
		return this.messageType;
	}

	/**
	 * Setter for the message's type
	 * 
	 * @param messageType
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}

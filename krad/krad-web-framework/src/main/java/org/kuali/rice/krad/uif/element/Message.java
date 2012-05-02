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

import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates a text message to be displayed
 * 
 * <p>
 * The <code>Message</code> is used to display static text in the user
 * interface
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Message extends ContentElementBase {
	private static final long serialVersionUID = 4090058533452450395L;

	private String messageText;

	public Message() {
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

}

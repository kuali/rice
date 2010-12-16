/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.test.ksb.testclient1;

import org.kuali.rice.test.shared.service.Message;
import org.kuali.rice.test.shared.service.MessageResource;


/**
 * service implementation for {@link MessageResource}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MessageResourceImpl implements MessageResource {

	private Storage storage;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.test.ksb.testclient1.MessageResource#createMessage(org.kuali.rice.test.ksb.testclient1.Message)
	 */
	public Message createMessage(Message message) {
		return storage.storeMessage(message);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.test.ksb.testclient1.MessageResource#delete(java.lang.String)
	 */
	public void delete(String id) {
		storage.deleteMessage(id);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.test.ksb.testclient1.MessageResource#retrieve(java.lang.String)
	 */
	public Message retrieve(String id) {
		return storage.retrieveMessage(id);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.test.ksb.testclient1.MessageResource#update(org.kuali.rice.test.ksb.testclient1.Message)
	 */
	public void update(Message message) {
		storage.storeMessage(message);
	}

	/**
	 * @return the storage
	 */
	public Storage getStorage() {
		return this.storage;
	}

	/**
	 * @param storage the storage to set
	 */
	public void setStorage(Storage storage) {
		this.storage = storage;
	}

}

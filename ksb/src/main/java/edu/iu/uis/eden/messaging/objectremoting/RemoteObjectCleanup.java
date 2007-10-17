/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging.objectremoting;

import javax.transaction.Synchronization;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * {@link Synchronization} to cleanup any remote objects created during the
 * current transaction.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoteObjectCleanup implements TransactionSynchronization {

	private static final Logger LOG = Logger.getLogger(RemoteObjectCleanup.class);

	private QName objectRemoterName;

	private QName serviceToRemove;

	public RemoteObjectCleanup(QName objectRemoterName, QName serviceToRemove) {
		this.setObjectRemoterName(objectRemoterName);
		this.setServiceToRemove(serviceToRemove);
	}

	public void afterCompletion(int status) {
		LOG.debug("Removing service: " + this.getServiceToRemove() + " from ObjectRemoter: " + this.getObjectRemoterName());
		ObjectRemoterService objectRemoter = (ObjectRemoterService) GlobalResourceLoader.getService(this.getObjectRemoterName());
		objectRemoter.removeService(this.getServiceToRemove());
	}

	public void beforeCompletion() {
	    // template method
	}

	public QName getObjectRemoterName() {
		return this.objectRemoterName;
	}

	public void setObjectRemoterName(QName objectRemoterName) {
		this.objectRemoterName = objectRemoterName;
	}

	public QName getServiceToRemove() {
		return this.serviceToRemove;
	}

	public void setServiceToRemove(QName serviceToRemove) {
		this.serviceToRemove = serviceToRemove;
	}

	public void afterCommit() {
	}

	public void beforeCommit(boolean readOnly) {
	}

	public void resume() {
	}

	public void suspend() {
	}
}
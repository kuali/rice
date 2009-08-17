/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.lookupable;

/**
 * Decorates a web-tier bean to give it necessary properties to allow for lookups.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebLookupableDecorator {

	private String returnUrl;
	private String actionsUrl;
	private String destinationUrl;

	public String getActionsUrl() {
		return actionsUrl;
	}
	public void setActionsUrl(String actionsUrl) {
		this.actionsUrl = actionsUrl;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
	public String getDestinationUrl() {
		return destinationUrl;
	}
	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}
}

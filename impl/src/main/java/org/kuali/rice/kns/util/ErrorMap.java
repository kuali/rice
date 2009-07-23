/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.util;




/**
 * @deprecated use org.kuali.rice.core.util.MessageMap instead
 * 
 * Holds errors due to validation. Keys of map represent property paths, and value is a TypedArrayList that contains resource string
 * keys (to retrieve the error message).
 *
 * Note, prior to rice 1.0.0, this class implemented {@link java.util.Map}.  The implements has been removed as of rice 0.9.4
 */
@Deprecated
public class ErrorMap extends MessageMap {
	
	public ErrorMap() {}
	
	public ErrorMap(MessageMap messageMap) {
		super(messageMap);
	}
	

}

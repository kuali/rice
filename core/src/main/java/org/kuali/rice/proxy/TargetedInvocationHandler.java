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
package org.kuali.rice.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * An InvocationHandler which targets a specific Object.  An InvocationHandler which implements this
 * interface should provide a reference to the target Object it's wrapping via the getTarget() method.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface TargetedInvocationHandler extends InvocationHandler {

	/**
	 * Returns the target object wrapped by this InvocationHandler.
	 */
	public Object getTarget();
	
}

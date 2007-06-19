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
package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be used to register an {@link AsynchronousCallback} to recieve callback
 * notifications.
 * 
 * @see AsynchronousCallback
 *
 * @author rkirkend
 */
public class GlobalCallbackRegistry {

	private static List<AsynchronousCallback> callbacks = new ArrayList<AsynchronousCallback>();

	public static List<AsynchronousCallback> getCallbacks() {
		return callbacks;
	}

	public static void setCallbacks(List<AsynchronousCallback> callbacks) {
		GlobalCallbackRegistry.callbacks = callbacks;
	}
}
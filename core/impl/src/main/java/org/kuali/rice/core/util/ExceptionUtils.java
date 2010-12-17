/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides some Utilities for handling common Exception scenarios.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ExceptionUtils {

	/**
	 * Unwraps the appropriate Throwable from the given Throwable.  More specifically, this handles
	 * unwrapping causes from InvocationTargetException.
	 * @param throwable
	 * @return
	 */
	public static Throwable unwrapActualCause(Throwable throwable) {
		Throwable unwrappedThrowable = null;
		if (throwable instanceof InvocationTargetException) {
			InvocationTargetException ite = (InvocationTargetException)throwable;
			if (ite.getCause() != null && ite.getCause() != ite) {
				unwrappedThrowable = ite.getCause();
			}
		}
		if (unwrappedThrowable != null && unwrappedThrowable != throwable) {
			return unwrapActualCause(unwrappedThrowable);
		}
		return throwable;
	}
	
	/**
	 * Searches the causes of the given Throwable to see if the given Throwable Class is among them;
	 * if so, returns that Exception, otherwise returns null
	 * 
	 * @param throwable the Throwable to search for causes in
	 * @param soughtCause the Throwable Class to search for in the cause stack
	 * @return the Throwable which is an instance of the sought for Throwable Class, or null if it could not be found
	 */
	public static Throwable searchForCause(Throwable throwable, Class<? extends Throwable> soughtCause) {
		if (throwable.getClass().equals(soughtCause)) return throwable;
		if (throwable.getCause() == null) return null;
		return searchForCause(throwable.getCause(), soughtCause);
	}
	
}

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
package org.kuali.rice.kew.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/*
 * A simple Enumeration implementation which is backed by a List and it's Iterator.
 * Provides a few convienance constructors for creating the Enumeration.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleEnumeration<T> implements Enumeration<T> {

	private List<T> internalList = new ArrayList<T>();
	private Iterator<T> iterator;
	
	public SimpleEnumeration(T object) {
		if (object != null) {
			internalList.add(object);
		}
		iterator = internalList.iterator();
	}
	
	public SimpleEnumeration(Enumeration<T> enumeration, T object) {
		while (enumeration.hasMoreElements()) {
			internalList.add(enumeration.nextElement());
		}
		internalList.add(object);
		iterator = internalList.iterator();
	}
	
	public SimpleEnumeration(Enumeration<T> enumeration1, Enumeration<T> enumeration2) {
		while (enumeration1.hasMoreElements()) {
			internalList.add(enumeration1.nextElement());
		}
		while (enumeration2.hasMoreElements()) {
			internalList.add(enumeration2.nextElement());
		}
		iterator = internalList.iterator();
	}
	
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	public T nextElement() {
		return iterator.next();
	}

}

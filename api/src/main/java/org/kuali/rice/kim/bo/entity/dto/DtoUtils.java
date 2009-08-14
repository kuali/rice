/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.rice.kns.bo.Defaultable;

/**
 * Contains common utility methods used for DTOs.  Currently package visible only.  This class could be moved to a more common
 * place to be used by other classes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
class DtoUtils {

	private DtoUtils() {
		throw new UnsupportedOperationException("do not call");
	}
	
	/**
	 *  Returns an empty string if the passed in string is null or the passed in string if not null.
	 *  @param str the string to unNullify 
	 *  @return a non-null string.
	 */
	public static String unNullify( String str ) {
		return str != null ? str : "";
	}
	
	/**
	 *  Returns an empty list if the passed in list is null or the passed in list if not null.
	 *  @param <T> the generic type of lists
	 *  @param list the list to unNullify 
	 *  @return a non-null list.
	 */
	public static <T> List<T> unNullify( List<T> list ) {
		return list != null ? list : new ArrayList<T>();
	}
	
	/**
	 *  Returns an new Date if the passed in date is null or the passed in date if not null.
	 *  @param dte the date to unNullify 
	 *  @return a non-null date.
	 */
	public static Date unNullify( Date dte ) {
		return dte != null ? dte : new Date();
	}
	
	/**
	 *  Returns an new object based on the class token if the passed in object is null. Returns the passed in object if not null.
	 *  The passed in class token MUST refer to a non-abstract class with a no-arg, visible ctor.
	 *  @param <T> the generic type of object
	 *  @param obj the object to unNullify
	 *  @param typ the class token for the object.  The an object of this type will be returned when the obj is null.
	 *  @return a non-null object.
	 *  @throws CreationException if unable to create an new object to return
	 */
	public static <T> T unNullify( T obj, Class<? extends T> typ ) {
		try {
			return obj != null ? obj : typ.newInstance();
		} catch (InstantiationException e) {
			throw new CreationException(e);
		} catch (IllegalAccessException e) {
			throw new CreationException(e);
		}
	}
	
	/**
	 *  Returns an new object based on the class token if the passed in list is null or if no default object exists.
	 *  Returns the default object from the list when found.
	 *  The passed in class token MUST refer to a non-abstract class with a no-arg, visible ctor.
	 *  @param <T> the generic type of object
	 *  @param list contains the default object to unNullify
	 *  @param typ the class token for the object.  The an object of this type will be returned when the obj is null.
	 *  @return a non-null object.
	 *  @throws CreationException if unable to create an new object to return
	 */
	public static <T extends Defaultable> T getDefaultAndUnNullify(List<? extends T> list, Class<? extends T> typ) {
		T toReturn = null;
		for (T item : unNullify(list)) {
			if (item.isDefault()) {
				toReturn = item;
			}
		}
		return unNullify(toReturn, typ);
	}
	
	/** Exception thrown when there is a problem creating an object. */
	static class CreationException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
		 * Wraps a Throwable.
		 * @param t the throwable
		 */
		CreationException(Throwable t) {
			super(t);
		}
	}
}

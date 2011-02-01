/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

/**
 * This is a generic dictionary entry for an object that does not have to implement BusinessObject. It provides support
 * for general objects as required by Kuali Student. 
 * 
 * @author James Renfro 
 */
public class ObjectDictionaryEntry extends DataDictionaryEntryBase{

	private String name;
	private Class<?> objectClass;
	
	/**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#getJstlKey()
     */
	@Override public String getJstlKey() {
        if (objectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: objectClass is null");
        }

        return (objectClass != null) ? objectClass.getSimpleName() : objectClass.getSimpleName();
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    @Override public String getFullClassName() {
        return objectClass.getName();
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    @Override public Class<?> getEntryClass() {
        return objectClass;
    }

	/**
	 * @return the objectClass
	 */
	public Class<?> getObjectClass() {
		return this.objectClass;
	}

	/**
	 * @param objectClass the objectClass to set
	 */
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}

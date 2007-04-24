/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class represents a parentClass/child relationship between two objects
 * This is used primarily by the <code>BusinessObjectMetaDataService
 */
public class BusinessObjectRelationship implements Serializable {
    private Class relatedClass;
    private Class parentClass;
    private String parentAttributeName;
    private Map<String,String> parentToChildReferences = new HashMap<String,String>( 4 );
    private String userVisibleIdentifierKey = null;
    
    public BusinessObjectRelationship() {
    }
    

    public BusinessObjectRelationship(Class parent, String parentAttributeName, Class relatedClass ) {
        super();
        this.relatedClass = relatedClass;
        this.parentClass = parent;
        this.parentAttributeName = parentAttributeName;
    }


    public Class getRelatedClass() {
        return this.relatedClass;
    }

    /**
     * Gets the parentClass attribute. 
     * @return Returns the parentClass.
     */
    public Class getParentClass() {
        return parentClass;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "Relationship: " ).append( parentClass.getName() ).append( " -> " ).append( relatedClass.getName() );
        for ( Map.Entry<String,String> refs : parentToChildReferences.entrySet() ) {
            sb.append( "\n   " ).append( refs.getKey() ).append( " -> " ).append( refs.getValue() );           
        }
        return sb.toString();
    }


    public String getParentAttributeName() {
        return parentAttributeName;
    }


    public Map<String, String> getParentToChildReferences() {
        return parentToChildReferences;
    }


    public void setParentToChildReferences(Map<String, String> referenceAttribues) {
        this.parentToChildReferences = referenceAttribues;
    }


	public String getUserVisibleIdentifierKey() {
		return userVisibleIdentifierKey;
	}


	public void setUserVisibleIdentifierKey(String userVisibleIdentifierKey) {
		this.userVisibleIdentifierKey = userVisibleIdentifierKey;
	}

}

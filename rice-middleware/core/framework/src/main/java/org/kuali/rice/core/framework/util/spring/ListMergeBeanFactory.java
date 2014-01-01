/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.framework.util.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

/**
 * Merges a list of lists into a single list for use in the spring context. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ListMergeBeanFactory implements FactoryBean<List> {

    private List mergedList = new ArrayList();
    
    /**
     * Merges the lists given into a single unmodifiable list which will be returned when this bean is accessed.
     */
    public ListMergeBeanFactory( List<List> lists ) {
        for ( List list : lists ) {
            if ( list != null ) {
                mergedList.addAll( list );
            }
        }
        mergedList = Collections.unmodifiableList(mergedList);
    }
    
    @Override
    public List<?> getObject() throws Exception {
        return mergedList;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return ArrayList.class;
    }
    
    /**
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    
}

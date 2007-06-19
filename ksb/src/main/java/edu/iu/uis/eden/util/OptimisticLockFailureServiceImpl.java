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
package edu.iu.uis.eden.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A basic implementation of this class which can be configured with optimistic locking exception
 * class names to recognize.
 * 
 * @author ewestfal
 */
public class OptimisticLockFailureServiceImpl implements OptimisticLockFailureService {
    
    private List<Class<?>> classes = new ArrayList<Class<?>>();
    
    public boolean checkForOptimisticLockFailure(Exception exception) {
        for (final Class<?> clazz : this.classes) {
            if (clazz.isInstance(exception) ||
                    clazz.isInstance(exception.getCause())) return true;
        }
        return false;
    }
    
    public void setClassNames(List classNames) throws ClassNotFoundException {
        this.classes = new ArrayList<Class<?>>();
        if (classNames != null) {
            for (Iterator iterator = classNames.iterator(); iterator.hasNext();) {
                String className = (String)iterator.next();
                Class exceptionClass = Class.forName(className);
                this.classes.add(exceptionClass);
            }
        }
    }
}

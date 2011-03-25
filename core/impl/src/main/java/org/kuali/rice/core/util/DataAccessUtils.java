/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.HashSet;
import java.util.Set;

import org.apache.ojb.broker.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class DataAccessUtils {

    private static final Set<Class<?>> OPTIMISTIC_LOCK_EXCEPTION_CLASSES = new HashSet<Class<?>>();
    
        private DataAccessUtils() {
                throw new UnsupportedOperationException("do not call");
        }

    // add some standard optimistic lock exception classes
    static {
        addOptimisticLockExceptionClass(OptimisticLockException.class);
        addOptimisticLockExceptionClass(OptimisticLockingFailureException.class);
    }

    public static boolean isOptimisticLockFailure(Exception exception) {
        if (exception == null) {
            return false;
        }
        for (final Class<?> exceptionClass : getOptimisticLockExceptionClasses()) {
            if (exceptionClass.isInstance(exception) || exceptionClass.isInstance(exception.getCause())) {
                return true;
            }
        }
        return false;
    }

    public static void addOptimisticLockExceptionClass(Class exceptionClass) {
        OPTIMISTIC_LOCK_EXCEPTION_CLASSES.add(exceptionClass);
    }

    public static Set<Class<?>> getOptimisticLockExceptionClasses() {
        return OPTIMISTIC_LOCK_EXCEPTION_CLASSES;
    }

}
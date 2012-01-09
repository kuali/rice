/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.core.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that binds/unbinds the context ClassLoader of the current Thread
 * using a ThreadLocal.
 * This class supports re-entrancy by maintaining a stack of context classloaders.
 * NOTE: maybe implement stricter checks, by matching some contextual object or original
 * classloader on bind to the unbind so that they are always matched (within a given context)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ContextClassLoaderBinder {
	
	/**
     * Stack of previous context classloaders that should be
     * restored on unbind
     */
    private static final ThreadLocal<List<ClassLoader>> STACK = new ThreadLocal<List<ClassLoader>>() {
        protected List<ClassLoader> initialValue() {
            return new ArrayList<ClassLoader>(5);
        }
    };

    private static List<ClassLoader> getStack() {
        return STACK.get();
    }

    public static void bind(ClassLoader cl) {
        List<ClassLoader> stack = getStack();
        Thread current = Thread.currentThread();
        //log.debug("[bind] Switching CCL from " + current.getContextClassLoader() + " to " + cl);
        // push the current context classloader on the stack
        stack.add(current.getContextClassLoader());
        current.setContextClassLoader(cl);
    }

    public static void unbind() {
        List<ClassLoader> stack = getStack();
        if (stack.size() == 0) {
            throw new IllegalStateException("No context classloader to unbind!");
        }
        // pop the last context classloader off the stack
        ClassLoader lastClassLoader = stack.get(stack.size() - 1);
        //log.debug("[unbind] Switching CCL from " + Thread.currentThread().getContextClassLoader() + " to " + lastClassLoader);
        stack.remove(stack.size() - 1);
        Thread.currentThread().setContextClassLoader(lastClassLoader);
    }
}

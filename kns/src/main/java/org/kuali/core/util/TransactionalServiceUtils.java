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
package org.kuali.core.util;

import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

/**
 * This class provides utility methods to support the operation of transactional services
 */
public class TransactionalServiceUtils {
    /**
     * Copys iterators so that they may be used outside of this class.  Often, the DAO may
     * return iterators that may not be used outside of this class because the transaction/
     * connection may be automatically closed by Spring.
     * 
     * This method copies all of the elements in the OJB backed iterators into list-based iterators
     * by placing the returned BOs into a list
     * 
     * @param iter an OJB backed iterator to copy
     * @return an Iterator that may be used outside of this class
     */
    public static <E> Iterator<E> copyToExternallyUsuableIterator(Iterator<E> iter) {
        return IteratorUtils.toList(iter).iterator();
    }
}

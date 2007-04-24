/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.datadictionary;


/**
 * Interface defining an object used to perform arbitrary operations on a collection of DocumentEntry instances.
 * 
 * 
 */
public interface DocumentEntryVisitor {
    /**
     * Performs some arbitrary operation on or with the given DocumentEntry instance.
     * 
     * @param documentEntry
     */
    public void visitEntry(DocumentEntry documentEntry);
}
/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.exceptions;

/**
 * Kuali can throw this when the database contains a nonexistent relation. The database should have referential integrity
 * constraints that would prevent this from ever happening. Therefore Kuali is not obliged to generally check for this and throw,
 * but it can.
 * 
 * 
 */
public class ReferentialIntegrityException extends RuntimeException {
    public ReferentialIntegrityException(String message) {
        super(message);
    }
}

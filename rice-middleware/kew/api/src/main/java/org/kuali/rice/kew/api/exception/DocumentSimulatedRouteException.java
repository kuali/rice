/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.api.exception;


/**
 * The exception used from the Simulation Engine when it has problems routing a
 * document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSimulatedRouteException extends WorkflowException {

    private static final long serialVersionUID = 8521549957308984549L;

    public DocumentSimulatedRouteException() {
        super();
    }

    public DocumentSimulatedRouteException(String s) {
        super(s);
    }

    public DocumentSimulatedRouteException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DocumentSimulatedRouteException(Throwable throwable) {
        super(throwable);
    }

}

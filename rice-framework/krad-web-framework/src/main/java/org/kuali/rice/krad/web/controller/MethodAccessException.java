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
package org.kuali.rice.krad.web.controller;

import org.kuali.rice.core.api.exception.RiceRuntimeException;

/**
 * Runtime exception thrown when a controller method is requested that is not accessible.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MethodAccessException extends RiceRuntimeException {
    private static final long serialVersionUID = -4173237177827444387L;

    private final Class<?> controllerClass;
    private final String methodToCall;

    public MethodAccessException(Class<?> controllerClass, String methodToCall) {
        super("Access is protected for controller: " + controllerClass.getName() + " and method: " + methodToCall);

        this.controllerClass = controllerClass;
        this.methodToCall = methodToCall;
    }

    /**
     * Class of the controller the method was requested for.
     *
     * @return controller class
     */
    public Class<?> getControllerClass() {
        return controllerClass;
    }

    /**
     * Name of the controller method that was requested.
     *
     * @return controller method name
     */
    public String getMethodToCall() {
        return methodToCall;
    }
}

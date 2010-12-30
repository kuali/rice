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
package org.kuali.rice.ksb.messaging.service;

import org.kuali.rice.ksb.messaging.PersistedMessageBO;

/**
 * A service for administrative functions for a node on the service bus.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface BusAdminService {

    /**
     * Forward the given message to this service for processing.
     */
    public void forward(PersistedMessageBO message) throws Exception;

    public void ping();

    public void setCorePoolSize(int corePoolSize);

    public void setMaximumPoolSize(int maxPoolSize);

    public void setConfigProperty(String propertyName, String propertyValue);

}

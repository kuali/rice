/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.ken.service;

import java.util.Collection;

import org.kuali.rice.ken.bo.NotificationContentType;

/**
 * Service for accessing {@link NotificationContentType}s
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface NotificationContentTypeService {
    /**
     * This method retrieves a NotificationContentType by name.
     * @param name The name of the content type
     * @return NotificationContentType
     */
    public NotificationContentType getNotificationContentType(String name);

    /**
     * This method saves a NotificationContentType object instance to the DB, creating a new, current,
     * version if one already exists.  Note that this means that this API cannot be used to modify
     * the data of an existing content type record.
     * @param contentType The NotificationContentType instance to save.
     */
    public void saveNotificationContentType(NotificationContentType contentType);

    /**
     * This method returns all current NotificationContentTypes in the system.
     * @return Collection
     */
    public Collection<NotificationContentType> getAllCurrentContentTypes();

    /**
     * This method returns all versions of all NotificationContentTypes in the system.
     * @return Collection
     */
    public Collection<NotificationContentType> getAllContentTypes();
}

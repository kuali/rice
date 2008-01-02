/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.service.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.notification.bo.NotificationContentType;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationContentTypeService;

/**
 * NotificationContentTypeService implementation - uses the businessObjectDao to get at the underlying data in the stock DBMS.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationContentTypeServiceImpl implements NotificationContentTypeService {
    private BusinessObjectDao businessObjectDao;

    /**
     * Constructs a NotificationContentTypeServiceImpl.java.
     * @param businessObjectDao
     */
    public NotificationContentTypeServiceImpl(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @see org.kuali.notification.service.NotificationContentTypeService#getNotificationContentType(java.lang.String)
     */
    public NotificationContentType getNotificationContentType(String name) {
        Criteria c = new Criteria();
        c.addEqualTo("name", name);
        c.addEqualTo("current", true);
        Collection<NotificationContentType> coll = businessObjectDao.findMatching(NotificationContentType.class, c);
        if (coll.size() == 0) {
            return null;
        } else {
            return coll.iterator().next();
        }
    }

    /**
     * @see org.kuali.notification.service.NotificationContentTypeService#saveNotificationContentType(org.kuali.notification.bo.NotificationContentType)
     */
    public void saveNotificationContentType(NotificationContentType contentType) {
        NotificationContentType old = getNotificationContentType(contentType.getName());
        int oldVersion = -1;
        if (old != null) {
            old.setCurrent(false);
            oldVersion = old.getVersion();
            businessObjectDao.save(old);
        }
        contentType.setVersion(oldVersion + 1);
        contentType.setCurrent(true);
        businessObjectDao.save(contentType);
    }

    /**
     * @see org.kuali.notification.service.NotificationContentTypeService#getAllContentType()
     */
    public Collection<NotificationContentType> getAllCurrentContentTypes() {
        Criteria c = new Criteria();
        c.addEqualTo("current", true);
        return businessObjectDao.findMatching(NotificationContentType.class, c);
    }

}
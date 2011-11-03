/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.ken.service.impl;

import org.kuali.rice.core.framework.persistence.dao.GenericDao;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.bo.NotificationContentType;
import org.kuali.rice.ken.service.NotificationContentTypeService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import org.apache.ojb.broker.query.QueryByCriteria;
//import org.apache.ojb.broker.query.QueryFactory;
//import org.kuali.rice.core.jpa.criteria.Criteria;


/**
 * NotificationContentTypeService implementation - uses the businessObjectDao to get at the underlying data in the stock DBMS.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationContentTypeServiceImpl implements NotificationContentTypeService {
    private GenericDao businessObjectDao;

    /**
     * Constructs a NotificationContentTypeServiceImpl.java.
     * @param businessObjectDao
     */
    public NotificationContentTypeServiceImpl(GenericDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getNotificationContentType(java.lang.String)
     */
    //this is the one need to tweek on criteria
    public NotificationContentType getNotificationContentType(String name) {
//        Criteria c = new Criteria();
//        c.addEqualTo("name", name);
//        c.addEqualTo("current", true);	
//    	Criteria c = new Criteria(NotificationContentType.class.getName());
//    	c.eq("name", name);
//    	c.eq("current", true);
    	Map<String, Object> c = new HashMap<String, Object>();
    	c.put("name", name);
    	c.put("current", new Boolean(true));
    	
        Collection<NotificationContentType> coll = businessObjectDao.findMatching(NotificationContentType.class, c);
        if (coll.size() == 0) {
            return null;
        } else {
            return coll.iterator().next();
        }
    }

    protected int findHighestContentTypeVersion(String name) {
        // there's probably a better way...'report'? or direct SQL
        Map<String, Object> fields = new HashMap<String, Object>(2);
        fields.put("name", name);
        Collection<NotificationContentType> types = businessObjectDao.findMatchingOrderBy(NotificationContentType.class, fields, "version", false);
        if (types.size() > 0) {
            return types.iterator().next().getVersion();
        }
        return -1;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#saveNotificationContentType(org.kuali.rice.ken.bo.NotificationContentType)
     */
    public void saveNotificationContentType(NotificationContentType contentType) {
        NotificationContentType previous = getNotificationContentType(contentType.getName());
        if (previous != null) {
            previous.setCurrent(false);
            businessObjectDao.save(previous);
        }
        int lastVersion = findHighestContentTypeVersion(contentType.getName());
        NotificationContentType next;
        if (contentType.getId() == null) {
            next = contentType; 
        } else {
            next = new NotificationContentType();
            next.setName(contentType.getName());
            next.setDescription(contentType.getDescription());
            next.setNamespace(contentType.getNamespace());
            next.setXsd(contentType.getXsd());
            next.setXsl(contentType.getXsl());
        }

        next.setVersion(lastVersion + 1);
        next.setCurrent(true);
        businessObjectDao.save(next);
        
        // update all the old references
        if (previous != null) {
            Collection<Notification> ns = getNotificationsOfContentType(previous);
            for (Notification n: ns) {
                n.setContentType(next);
                businessObjectDao.save(n);
            }
        }
    }

    protected Collection<Notification> getNotificationsOfContentType(NotificationContentType ct) {
        Map<String, Object> fields = new HashMap<String, Object>(1);
        fields.put("contentType", ct.getId());
        return businessObjectDao.findMatching(Notification.class, fields);
    }
    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getAllCurrentContentTypes()
     */
    public Collection<NotificationContentType> getAllCurrentContentTypes() {
//        Criteria c = new Criteria();
//        c.addEqualTo("current", true);
////    	Criteria c = new Criteria(NotificationContentType.class.getName());
////    	c.eq("current", true);
    	
    	Map<String, Boolean> c = new HashMap<String, Boolean>();
    	c.put("current", new Boolean(true));
   
        return businessObjectDao.findMatching(NotificationContentType.class, c);
    }
    
    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getAllContentTypes()
     */
    public Collection<NotificationContentType> getAllContentTypes() {
        return businessObjectDao.findAll(NotificationContentType.class);
    }
}

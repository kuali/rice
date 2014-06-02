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
package org.kuali.rice.ken.service.impl;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.greaterThanOrEqual;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationContentTypeBo;
import org.kuali.rice.ken.service.NotificationContentTypeService;
import org.kuali.rice.krad.data.DataObjectService;

/**
 * NotificationContentTypeService implementation - uses the dataObjectService to get at the underlying data in the stock DBMS.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationContentTypeServiceImpl implements NotificationContentTypeService {

    /** Service to persist data to and from the datasource. */
    private DataObjectService dataObjectService;

    /**
     * Constructs a NotificationContentTypeServiceImpl.java.
     * @param dataObjectService Service to persist data to and from the datasource.
     */
    public NotificationContentTypeServiceImpl(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getNotificationContentType(java.lang.String)
     */
    //this is the one need to tweek on criteria
    @Override
    public NotificationContentTypeBo getNotificationContentType(String name) {
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name is blank");
        }

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("name", name), equal("current", new Boolean(true)));
        List<NotificationContentTypeBo> coll= dataObjectService.findMatching(NotificationContentTypeBo.class, criteria.build()).getResults();
        if (coll.isEmpty()) {
            return null;
        } else {
            return coll.get(0);
        }
    }

    /**
     * Returns the highest version found for the given name or negative one if the name is not found.
     * @param name the name to query for
     * @return the highest version number found or negative one if the name is not found.
     */
    protected int findHighestContentTypeVersion(String name) {

        int highestVersion = -1;

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("name", name));
        List<NotificationContentTypeBo> ntfyCntTypes = dataObjectService.findMatching(NotificationContentTypeBo.class, criteria.build()).getResults();

        for (NotificationContentTypeBo ntfyCntType : ntfyCntTypes) {
            if (ntfyCntType.getVersion() > highestVersion) {
                highestVersion = ntfyCntType.getVersion();
            }
        }

        return highestVersion;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#saveNotificationContentType(org.kuali.rice.ken.bo.NotificationContentTypeBo)
     */
    @Override
    public void saveNotificationContentType(NotificationContentTypeBo contentType) {
        NotificationContentTypeBo previous = getNotificationContentType(contentType.getName());
        if (previous != null) {
            previous.setCurrent(false);
            previous = dataObjectService.save(previous);
        }
        int lastVersion = findHighestContentTypeVersion(contentType.getName());
        NotificationContentTypeBo next;
        if (contentType.getId() == null) {
            next = contentType;
        } else {
            next = new NotificationContentTypeBo();
            next.setName(contentType.getName());
            next.setDescription(contentType.getDescription());
            next.setNamespace(contentType.getNamespace());
            next.setXsd(contentType.getXsd());
            next.setXsl(contentType.getXsl());
        }

        next.setVersion(lastVersion + 1);
        next.setCurrent(true);
        next = dataObjectService.save(next);

        // update all the old references
        if (previous != null) {
            Collection<NotificationBo> ns = getNotificationsOfContentType(previous);
            for (NotificationBo n: ns) {
                n.setContentType(next);
                dataObjectService.save(n);
            }
        }
    }

    /**
     * Get notifications based on content type.
     * @param ct Notification content type
     * @return  a collection of {@link NotificationBo} for the given content type
     */
    protected Collection<NotificationBo> getNotificationsOfContentType(NotificationContentTypeBo ct) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("contentType.id", ct.getId()));
        return dataObjectService.findMatching(NotificationBo.class, criteria.build()).getResults();
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getAllCurrentContentTypes()
     */
    @Override
    public Collection<NotificationContentTypeBo> getAllCurrentContentTypes() {
    	QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("current", new Boolean(true)));

        return dataObjectService.findMatching(NotificationContentTypeBo.class, criteria.build()).getResults();
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationContentTypeService#getAllContentTypes()
     */
    @Override
    public Collection<NotificationContentTypeBo> getAllContentTypes() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(greaterThanOrEqual("version", 0));

        return dataObjectService.findMatching(NotificationContentTypeBo.class, criteria.build()).getResults();
    }

    /**
     * Sets the data object service
     * @param dataObjectService {@link DataObjectService}
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}

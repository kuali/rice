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

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.ken.bo.NotificationChannelBo;
import org.kuali.rice.ken.service.NotificationChannelService;
import org.kuali.rice.krad.data.DataObjectService;

import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNotNull;

/**
 * NotificationChannelService implementation - uses the businessObjectDao to get at data in the underlying database.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationChannelServiceImpl implements NotificationChannelService {
    private DataObjectService dataObjectService;

    /**
     * Constructs a NotificationChannelServiceImpl.java.
     * @param dataObjectService service persists data to datasource.
     */
    public NotificationChannelServiceImpl(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationChannelService#getNotificationChannel(java.lang.String)
     */
    @Override
    public NotificationChannelBo getNotificationChannel(String id) {

        return dataObjectService.find(NotificationChannelBo.class, Long.valueOf(id));
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationChannelService#getNotificationChannelByName(java.lang.String)
     */
    @Override
    public NotificationChannelBo getNotificationChannelByName(String name) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();

        criteria.setPredicates(equal("name", name));
        List<NotificationChannelBo> found = dataObjectService.findMatching(NotificationChannelBo.class, criteria.build()).getResults();
        assert(found.size() <= 1);

        return ((found.isEmpty() ? null : found.get(0)));
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationChannelService#getSubscribableChannels()
     */
    @Override
    public Collection getSubscribableChannels() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("subscribable", Boolean.TRUE));
        criteria.setOrderByAscending("name");

        return dataObjectService.findMatching(NotificationChannelBo.class, criteria.build()).getResults();
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationChannelService#getAllNotificationChannels()
     */
    @Override
    public Collection getAllNotificationChannels() {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setOrderByAscending("name");

        return dataObjectService.findMatching(NotificationChannelBo.class, criteria.build()).getResults();
    }
}

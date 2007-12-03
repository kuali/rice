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
package org.kuali.notification.dao;

import java.util.HashMap;

import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationChannelReviewer;
import org.kuali.notification.test.util.MockObjectsUtil;
import org.kuali.notification.util.NotificationConstants;

/**
 * This class tests basic persistence for the {@link NotificationChannelReviewer} business object.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationChannelReviewerDaoTest extends BusinessObjectPersistenceTestCaseBase {
    private NotificationChannel mockChannel1 = MockObjectsUtil.getTestChannel1();
    private NotificationChannelReviewer mockReviewer = MockObjectsUtil.buildTestNotificationChannelReviewer(NotificationConstants.RECIPIENT_TYPES.USER, "aReviewer");
    
    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#setup()
     */
    @Override
    protected void setup() {
        super.setup();
        businessObjectDao.save(mockChannel1);
    }

    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#delete()
     */
    @Override
    protected boolean delete() {
        HashMap criteria = new HashMap();
        
        criteria.put(NotificationConstants.BO_PROPERTY_NAMES.REVIEWER_ID, mockReviewer.getReviewerId());
        NotificationChannelReviewer reviewer = (NotificationChannelReviewer) businessObjectDao.findByUniqueKey(NotificationChannelReviewer.class, criteria);

        try {
            businessObjectDao.delete(reviewer);
        } catch(Exception e) {
            return false;
        }
        return true;
    }


    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#insert()
     */
    @Override
    protected boolean insert() {
        mockReviewer.setChannel(mockChannel1);
        
        try {
            businessObjectDao.save(mockReviewer);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#retrieve()
     */
    @Override
    protected boolean retrieve() {
        HashMap criteria = new HashMap();
        
        criteria.put(NotificationConstants.BO_PROPERTY_NAMES.REVIEWER_ID, mockReviewer.getReviewerId());
        NotificationChannelReviewer reviewer = (NotificationChannelReviewer) businessObjectDao.findByUniqueKey(NotificationChannelReviewer.class, criteria);

        boolean success = true;
        
        success &= reviewer != null;
        success &= reviewer.getReviewerId().equals(mockReviewer.getReviewerId());
        success &= reviewer.getReviewerType().equals(mockReviewer.getReviewerType());
        success &= reviewer.getChannel() != null;
        success &= reviewer.getChannel().getId().equals(mockChannel1.getId());

        return success;
    }

    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#update()
     */
    @Override
    protected boolean update() {
        HashMap criteria = new HashMap();
        
        criteria.put(NotificationConstants.BO_PROPERTY_NAMES.REVIEWER_ID, mockReviewer.getReviewerId());
        NotificationChannelReviewer reviewer = (NotificationChannelReviewer) businessObjectDao.findByUniqueKey(NotificationChannelReviewer.class, criteria);

        reviewer.setReviewerId("updatedReviewerId");
        reviewer.setReviewerType(NotificationConstants.RECIPIENT_TYPES.GROUP);

        try {
            businessObjectDao.save(reviewer);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.notification.dao.BusinessObjectPersistenceTestCaseBase#validateChanges()
     */
    @Override
    protected boolean validateChanges() {
        NotificationChannelReviewer reviewer = (NotificationChannelReviewer) businessObjectDao.findById(NotificationChannelReviewer.class, mockReviewer.getId());

        boolean success = reviewer != null;
        success &= reviewer.getReviewerId().equals("updatedReviewerId");
        success &= reviewer.getReviewerType().equals(NotificationConstants.RECIPIENT_TYPES.GROUP);
        
        return success;
    }
}

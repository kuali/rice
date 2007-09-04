/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.notification.core;

import java.sql.Timestamp;

import junit.framework.TestCase;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.junit.Ignore;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.util.NotificationConstants;

/**
 * Scratch test case for testing OJB SQL generation from query API
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
@Ignore
public class TestOJBTest extends TestCase {
    public void testCriteria() {
        Criteria criteria_STATUS = new Criteria();
        criteria_STATUS.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED);

        Criteria criteria_UNDELIVERED = new Criteria();
        criteria_UNDELIVERED.addEqualTo(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);

        // now OR the above two together
        criteria_STATUS.addOrCriteria(criteria_UNDELIVERED);

        Criteria criteria_NOTLOCKED = new Criteria();
        criteria_NOTLOCKED.addIsNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);

        Criteria fullQueryCriteria = new Criteria();
        fullQueryCriteria.addAndCriteria(criteria_NOTLOCKED);
        fullQueryCriteria.addLessOrEqualThan(NotificationConstants.BO_PROPERTY_NAMES.NOTIFICATION_AUTO_REMOVE_DATE_TIME, new Timestamp(System.currentTimeMillis()));
        // now add in the STATUS check
        fullQueryCriteria.addAndCriteria(criteria_STATUS);
        
        
        System.err.println(fullQueryCriteria.toString());
        
        Query q = QueryFactory.newQuery(Notification.class, fullQueryCriteria);
        System.err.println(q.toString());
        
    }
}

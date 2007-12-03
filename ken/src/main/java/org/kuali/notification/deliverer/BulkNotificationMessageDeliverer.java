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
package org.kuali.notification.deliverer;

import java.util.Collection;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.exception.NotificationAutoRemoveException;
import org.kuali.notification.exception.NotificationMessageDeliveryException;

/**
 * A NotificationMessageDeliverer interface specialization that should be implemented
 * by deliverers which can deliver messages in bulk.  This interface needs to exist
 * distinct from NotificationMessageDeliverer because processing in the two cases
 * will be different.  In the bulk case, the deliveries will be performed in a single
 * transaction.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface BulkNotificationMessageDeliverer extends NotificationMessageDeliverer {
    /**
     * This method is responsible for delivering a series of messageDelivery records
     * @param messageDeliveries The messageDeliveries to process
     * @throws NotificationMessageDeliveryException
     */
    public void deliverMessage(Collection<NotificationMessageDelivery> messageDeliveries) throws NotificationMessageDeliveryException;
    
    /**
     * This method handles auto removing message deliveries
     * @param messageDelivery The messageDeliveries to auto remove
     * @throws NotificationAutoRemoveException
     */
    public void autoRemoveMessageDelivery(Collection<NotificationMessageDelivery> messageDeliveries) throws NotificationAutoRemoveException;
}
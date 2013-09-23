/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.uif.util;

import java.util.Collection;
import java.util.Collections;

import org.kuali.rice.krad.messages.Message;
import org.kuali.rice.krad.messages.MessageService;

/**
 * Provides mock messages for UIF unit tests.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockMessageService implements MessageService {

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessage(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public Message getMessage(String namespace, String component, String key) {
        Message rv = new Message();
        rv.setNamespaceCode(namespace);
        rv.setComponentCode(component);
        rv.setKey(key);
        rv.setText(getMessageText(namespace, component, key));
        return rv;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessage(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Message getMessage(String namespace, String component, String key, String locale) {
        Message rv = new Message();
        rv.setNamespaceCode(namespace);
        rv.setComponentCode(component);
        rv.setKey(key);
        rv.setLocale(locale);
        rv.setText(namespace + ":" + component + ":" + key + ":" + locale);
        return rv;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public String getMessageText(String namespace, String component, String key) {
        return namespace + ":" + component + ":" + key;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getMessageText(String namespace, String component, String key, String locale) {
        return namespace + ":" + component + ":" + key + ":" + locale;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String)
     */
    @Override
    public String getMessageText(String key) {
        return key;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String getMessageText(String key, String locale) {
        return key + ":" + locale;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getAllMessagesForComponent(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Collection<Message> getAllMessagesForComponent(String namespace, String component) {
        Message rv = new Message();
        rv.setNamespaceCode(namespace);
        rv.setComponentCode(component);
        rv.setText(namespace + ":" + component);
        return Collections.singletonList(rv);
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getAllMessagesForComponent(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public Collection<Message> getAllMessagesForComponent(String namespace, String component, String locale) {
        Message rv = new Message();
        rv.setNamespaceCode(namespace);
        rv.setComponentCode(component);
        rv.setLocale(locale);
        rv.setText(namespace + ":" + component + ":" + locale);
        return Collections.singletonList(rv);
    }

}

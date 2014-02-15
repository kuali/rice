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
package org.kuali.rice.krad.uif.util;

import java.util.Collection;
import java.util.Collections;

import org.kuali.rice.krad.messages.Message;
import org.kuali.rice.krad.messages.MessageProvider;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.messages.MessageServiceImpl;
import org.kuali.rice.krad.messages.providers.ResourceMessageProvider;

/**
 * Provides mock messages for UIF unit tests.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockMessageService implements MessageService {

    private MessageService delegate;

    private MessageService getDelegate() {
        if (delegate != null) {
            return delegate;
        }

        ResourceMessageProvider provider = new ResourceMessageProvider();
        
        MessageServiceImpl messageServiceDelegate = new MessageServiceImpl();
        messageServiceDelegate.setMessageProviders(Collections.<MessageProvider> singletonList(provider));
        delegate = messageServiceDelegate;
        return delegate;
    }
    
    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessage(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public Message getMessage(String namespace, String component, String key) {
        Message rv = getDelegate().getMessage(namespace, component, key);
        if (rv != null) {
            return rv;
        }
        
        rv = new Message();
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
        Message rv = getDelegate().getMessage(namespace, component, key, locale);
        if (rv != null) {
            return rv;
        }

        rv = new Message();
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
        String rv = getDelegate().getMessageText(namespace, component, key);
        if (rv != null) {
            return rv;
        }

        return namespace + ":" + component + ":" + key;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getMessageText(String namespace, String component, String key, String locale) {
        String rv = getDelegate().getMessageText(namespace, component, key, locale);
        if (rv != null) {
            return rv;
        }

        return namespace + ":" + component + ":" + key + ":" + locale;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String)
     */
    @Override
    public String getMessageText(String key) {
        String rv = getDelegate().getMessageText(key);
        if (rv != null) {
            return rv;
        }

        return key;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getMessageText(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String getMessageText(String key, String locale) {
        String rv = getDelegate().getMessageText(key, locale);
        if (rv != null) {
            return rv;
        }

        return key + ":" + locale;
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getAllMessagesForComponent(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Collection<Message> getAllMessagesForComponent(String namespace, String component) {
        Collection<Message> rv = getDelegate().getAllMessagesForComponent(namespace, component);
        if (rv != null) {
            return rv;
        }

        Message rm = new Message();
        rm.setNamespaceCode(namespace);
        rm.setComponentCode(component);
        rm.setText(namespace + ":" + component);
        return Collections.singletonList(rm);
    }

    /**
     * @see org.kuali.rice.krad.messages.MessageService#getAllMessagesForComponent(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public Collection<Message> getAllMessagesForComponent(String namespace, String component, String locale) {
        Collection<Message> rv = getDelegate().getAllMessagesForComponent(namespace, component, locale);
        if (rv != null) {
            return rv;
        }

        Message rm = new Message();
        rm.setNamespaceCode(namespace);
        rm.setComponentCode(component);
        rm.setLocale(locale);
        rm.setText(namespace + ":" + component + ":" + locale);
        return Collections.singletonList(rm);
    }

}

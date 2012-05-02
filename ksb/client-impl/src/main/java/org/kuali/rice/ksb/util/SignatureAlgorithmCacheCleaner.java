/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.ksb.util;

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.kuali.rice.core.framework.util.ApplicationThreadLocal;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Removes {@link SignatureAlgorithm} ThreadLocals
 */
public class SignatureAlgorithmCacheCleaner implements ApplicationContextAware {
    private static final Logger LOG = Logger.getLogger(SignatureAlgorithmCacheCleaner.class);

    /**
     * Registers a listener that removes SignatureAlgorithm key caches on context shutdown
     * @param applicationContext spring context
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // register a context close handler
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
            context.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
                @Override
                public synchronized void onApplicationEvent(ContextClosedEvent e) {
                    LOG.info("Context '" + e.getApplicationContext().getDisplayName() + "' closed, removing SignatureAlgorithm caches");

                    // obtain the thread local caches via reflection
                    String[] threadLocalFields = new String[] { "instancesSigning", "instancesVerify", "keysSigning", "keysVerify" };
                    Collection<ThreadLocal> threadLocals = new ArrayList<ThreadLocal>(threadLocalFields.length);
                    for (String fname: threadLocalFields) {
                        try {
                            Field f = SignatureAlgorithm.class.getDeclaredField(fname);
                            if (f != null) {
                                f.setAccessible(true);
                                ThreadLocal tl = (ThreadLocal) f.get(null);
                                if (tl != null) {
                                    threadLocals.add(tl);
                                }
                            }
                        } catch (NoSuchFieldException nsme) {
                            nsme.printStackTrace();
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    }

                    // remove the thread locals from every thread
                    if (!threadLocals.isEmpty()) {
                        for (Thread t: Thread.getAllStackTraces().keySet()) {
                            for (ThreadLocal tl: threadLocals) {
                                ApplicationThreadLocal.removeThreadLocal(t, tl);
                            }
                        }
                    }
                }
            });
        }
    }
}

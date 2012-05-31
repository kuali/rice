/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.jpa.spring;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Null EntityManagerFactory that enables SharedEntityManagerBean to create a proxy when jpa is not enabled. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NullEntityManagerFactory implements EntityManagerFactory {

    /**
     * @see javax.persistence.EntityManagerFactory#close()
     */
    public void close() {
        throw new UnsupportedOperationException("JPA is not enabled, this should not be called.");
    }

    /**
     * @see javax.persistence.EntityManagerFactory#createEntityManager()
     */
    public EntityManager createEntityManager() {
        throw new UnsupportedOperationException("JPA is not enabled, this should not be called.");
    }

    /**
     * @see javax.persistence.EntityManagerFactory#createEntityManager(java.util.Map)
     */
    public EntityManager createEntityManager(Map arg0) {
        throw new UnsupportedOperationException("JPA is not enabled, this should not be called.");
    }

    /**
     * @see javax.persistence.EntityManagerFactory#isOpen()
     */
    public boolean isOpen() {
        throw new UnsupportedOperationException("JPA is not enabled, this should not be called.");
    }

}

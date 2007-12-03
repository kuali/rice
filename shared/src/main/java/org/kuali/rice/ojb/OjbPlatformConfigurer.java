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
// Created on Oct 11, 2006

package org.kuali.rice.ojb;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility class/bean that sets the platform OJB JDBC Connection Descriptors programmatically
 * at run time, so that it can be parameterized by Spring/Workflow configuration.
 * A map of jcdalias/datasource bean names to OJB platforms is accepted as the 'platforms' propery.
 * An empty key, or "DEFAULT" key is interpreted as specifying the default PersistenceBroker/jcdalias
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OjbPlatformConfigurer implements InitializingBean {
    private static final Logger LOG = Logger.getLogger(OjbPlatformConfigurer.class);

    /**
     * Literal to accept as specifying the "default" PersistenceBroker/jcdalias
     */
    private static final String DEFAULT = "DEFAULT";

    private Map<String, String> dsToPlatform;

    /**
     * Sets jcdAlias-&gt;platform string map.
     * @param platforms the map of jcdAlias-&gt;OJB platform string
     */
    public void setPlatforms(Map<String, String> platforms) {
        this.dsToPlatform = platforms;
    }

    public void afterPropertiesSet() throws Exception {
        configureOJBConnectionDescriptors(this.dsToPlatform);
    }

    /**
     * Configures the default OJB connection descriptor to use the specified platform
     * @param platform the OJB platform string
     */
    public static void configureDefaultOJBConnectionDescriptor(String platform) {
        configureOJBConnectionDescriptor("", platform);
    }

    /**
     * Configures the specified OJB connection descriptor to use the specified platform
     * @param jcdAlias the jcd alias
     * @param platform the OJB platform string
     */
    public static void configureOJBConnectionDescriptor(String jcdAlias, String platform) {
        Map<String, String> platforms = new HashMap<String, String>();
        platforms.put(jcdAlias, platform);
        configureOJBConnectionDescriptors(platforms);
    }

    /**
     * Configures the OJB connection descriptors with the platforms specified in the
     * given Map<String, String> which is taken as a map of jcdAlias-&gt;platform string.
     * The jcdAlias <code>""</code> or <code>"DEFAULT"</code> ({@link #DEFAULT}) is
     * interpreted as the default connection descriptor.
     * @param platforms the map of jcdAlias-&gt;OJB platform string
     */
    public static void configureOJBConnectionDescriptors(Map<String, String> platforms) {
        int numUpdated = 0;
        if (platforms != null) {        	
            // no way to obtain all possible PersistenceBroker/jcdalias keys from OJB? shame.
            for (Map.Entry<String, String> entry: platforms.entrySet()) {
                String key = entry.getKey();
                PersistenceBroker pb;
                // empty key means the default persistence broker/jcd alias
                boolean deflt = key.length() == 0 || DEFAULT.equals(key);
                if (deflt) {
                    pb = PersistenceBrokerFactory.defaultPersistenceBroker();
                } else {
                    pb = PersistenceBrokerFactory.createPersistenceBroker(new PBKey(key));
                }
                // PersisenceBroker and JCD alias are one and the same I guess
                LOG.info("Setting " + (deflt ? "<<<default>>>" : "'" + key + "'") + " OJB connection descriptor database platform to '" + entry.getValue() + "'");
                pb.serviceConnectionManager().getConnectionDescriptor().setDbms(entry.getValue());
                numUpdated++;
            }
        }
        if (numUpdated > 0) {
            LOG.info("Updated " + numUpdated + " OJB JDBC connection descriptors");
        } else {
            LOG.warn("No OJB JDBC connection descriptors updated");
        }
    }
}
/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.test.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.rice.test.lifecycles.JettyServerLifecycle.ConfigMode;

/**
 * Annotation for bringing up an embedded JettyServer in unit tests 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JettyServer {
    /**
     * Indicate that the port should be pulled from a configuration parameter
     */
    public static final int CONFIG_PARAM_PORT = -1;

    /**
     * The webapp file location, relative to the working directory of the unit test
     */
    String webapp() default "../src/test/webapp";
    /**
     * The context name; this will be prepended with a slash "/", so don't prepend a slash
     */
    String context() default "SampleRiceClient";
    /**
     * The port to bring jetty up on; CONFIG_PARAM_PORT to refer to a port from a config param
     */
    int port() default CONFIG_PARAM_PORT;
    /**
     * The name of the config param from which to read the port
     */
    String portConfigParam() default "kns.port";
    /**
     * What to do with the webapp's Config: NONE - nothing,
     * OVERRIDE - replace the current context config (the test harness config) with the webapp's config
     * MERGE - merge the properties and objects into the current context config (but don't replace the whole config)
     */
    ConfigMode configMode() default ConfigMode.OVERRIDE;
    /**
     * Whether to explicitly add the webapp's ResourceLoader to the GlobalResourceLoader 
     */
    boolean addWebappResourceLoader() default true;
}

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
package org.kuali.rice.ksb.messaging.serviceconnectors;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.ClassLoaderUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains some utility methods for dealing with configuration of the HttpComponents HttpClient.
 *
 * <p>To limit the impact of transitioning from Commons HttpClient to HttpComponents, certain legacy parameters
 * (namely, those that map over directly) are enumerated in this class.  There are methods here to help retrieve
 * those config param values as well.</p>
 *
 * <p>NOTE: The full list of supported parameters can be found in the source for this class.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum HttpClientParams {

    //
    // from org.apache.commons.httpclient.params.HttpMethodParams:
    //

    USE_EXPECT_CONTINUE("http.protocol.expect-continue", Boolean.class),
    HTTP_CONTENT_CHARSET("http.protocol.content-charset"),
    COOKIE_POLICY("http.protocol.cookie-policy"),

    //
    // from org.apache.commons.httpclient.params.HttpConnectionParams:
    //

    SO_TIMEOUT("http.socket.timeout", Integer.class),
    TCP_NODELAY("http.tcp.nodelay", Boolean.class),
    SO_SNDBUF("http.socket.sendbuffer", Integer.class),
    SO_RCVBUF("http.socket.receivebuffer", Integer.class),
    SO_LINGER("http.socket.linger", Integer.class),
    CONNECTION_TIMEOUT("http.connection.timeout", Integer.class),
    STALE_CONNECTION_CHECK("http.connection.stalecheck", Boolean.class),

    //
    // from org.apache.commons.httpclient.params.HttpConnectionManagerParams:
    //

    MAX_TOTAL_CONNECTIONS("http.connection-manager.max-total", Integer.class),

    //
    // from org.apache.commons.httpclient.params.HttpClientParams:
    //

    CONNECTION_MANAGER_TIMEOUT("http.connection-manager.timeout", Integer.class),
    REJECT_RELATIVE_REDIRECT("http.protocol.reject-relative-redirect", Boolean.class),
    MAX_REDIRECTS("http.protocol.max-redirects", Integer.class),
    ALLOW_CIRCULAR_REDIRECTS("http.protocol.allow-circular-redirects", Boolean.class);

    private String paramName;
    private Class paramValueClass;

    private static final Set<String> supportedParamNames = new HashSet<String>();

    private HttpClientParams(String paramName, Class paramValueClass) {
        this.paramName = paramName;
        this.paramValueClass = paramValueClass;
    }

    private HttpClientParams(String paramName) {
        this(paramName, String.class);
    }

    public <T> T getValue() {
        return getValueOrDefault(null);
    }

    public <T> T getValueOrDefault(T defaultValue) {
        T value = null;
        String strValue = ConfigContext.getCurrentContextConfig().getProperty(getParamName());

        if (strValue == null) {
            return defaultValue;
        }

        Class<?> paramType = getParamValueClass();

        if (paramType.equals(Boolean.class)) {
            value = (T) ConfigContext.getCurrentContextConfig().getBooleanProperty(getParamName());
        } else if (paramType.equals(Integer.class)) {
            value = (T) Integer.valueOf(strValue);
        } else if (paramType.equals(Long.class)) {
            value = (T) Long.valueOf(strValue);
        } else if (paramType.equals(Double.class)) {
            value = (T) Double.valueOf(strValue);
        } else if (paramType.equals(String.class)) {
            value = (T) strValue;
        } else if (paramType.equals(Class.class)) {
            try {
                value = (T) Class.forName(ConfigContext.getCurrentContextConfig().getProperty(getParamName()),
                        true, ClassLoaderUtils.getDefaultClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not locate the class needed to configure the HttpClient.", e);
            }
        } else {
            throw new RuntimeException("Attempted to configure an HttpClient parameter '" + getParamName() + "' " +
                    "of a type not supported through Workflow configuration: " + getParamValueClass().getName());
        }

        // this may be redundant except in weird cases
        if (value == null) {
            return defaultValue;
        }

        return value;
	}

    public String getParamName() {
        return paramName;
    }

    public Class getParamValueClass() {
        return paramValueClass;
    }
}

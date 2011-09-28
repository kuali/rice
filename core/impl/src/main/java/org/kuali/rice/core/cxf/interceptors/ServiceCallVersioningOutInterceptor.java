package org.kuali.rice.core.cxf.interceptors;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A CXF Interceptor that binds itself to the USER_PROTOCOL phase to be used on outbound
 * messages.   The role of this interceptor is to populate outgoing protocol headers
 * (for all intents and purposes, HTTP headers) with Kuali Rice and application version
 * information.
 * @see <a href="http://cxf.apache.org/docs/interceptors.html">CXF interceptors</a>
 */
public class ServiceCallVersioningOutInterceptor extends AbstractPhaseInterceptor<Message> {
    /**
     * Instantiates an ServiceCallVersioningOutInterceptor and adds it to the USER_PROTOCOL phase.
     */
    public ServiceCallVersioningOutInterceptor() {
        super(Phase.USER_PROTOCOL);
    }

    /**
     * Publishes the Kuali Rice Environment, Rice Version, Application Name and Application Version
     * in outbound protocol headers
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            ServiceCallVersioningHelper.populateVersionHeaders(headers);
        }
    }
}

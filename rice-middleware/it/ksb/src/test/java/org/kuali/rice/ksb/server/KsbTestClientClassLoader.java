package org.kuali.rice.ksb.server;

import org.kuali.rice.core.api.util.ShadowingInstrumentableClassLoader;
import org.kuali.rice.ksb.messaging.remotedservices.ServiceCallInformationHolder;

/**
 * A classloader used for KSB test clients.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KsbTestClientClassLoader extends ShadowingInstrumentableClassLoader {

    public KsbTestClientClassLoader() {
        super(KsbTestClientClassLoader.class.getClassLoader());
        excludeClass(ServiceCallInformationHolder.class.getName());
    }

    @Override
    protected boolean isExcluded(String className) {
        // by default, this classloader excludes org.eclipse which causes us issues with JPA, of course!
        if (className.startsWith("org.eclipse")) {
            return false;
        }
        // in order for jax-rs and Apache CXF to work properly when you are running multiple instances of CXF on the
        // same JVM but different classloaders, we must not defer to the parent classloader for anything
        // under javax.ws.rs.* packages
        if (className.startsWith("javax.ws.rs.")) {
            return false;
        }
        return super.isExcluded(className);
    }

}

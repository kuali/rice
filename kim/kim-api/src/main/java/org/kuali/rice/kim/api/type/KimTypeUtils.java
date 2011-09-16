package org.kuali.rice.kim.api.type;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;

import javax.xml.namespace.QName;

public final class KimTypeUtils {

    private KimTypeUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Resolves the given kim type service name represented as a String to the appropriate QName.
     * If the value given is empty or null, then it will resolve to a qname representing the
     * {@link KimConstants#DEFAULT_KIM_TYPE_SERVICE}.
     *
     * @param kimTypeServiceName the name to resolve
     * @return a qname representing a resolved type service
     */
    public static QName resolveKimTypeServiceName(String kimTypeServiceName) {
        if (StringUtils.isBlank(kimTypeServiceName)) {
            return QName.valueOf(KimConstants.DEFAULT_KIM_TYPE_SERVICE);
        }
        return QName.valueOf(kimTypeServiceName);
    }
}

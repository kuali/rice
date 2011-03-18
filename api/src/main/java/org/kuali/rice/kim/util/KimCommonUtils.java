package org.kuali.rice.kim.util;

import org.apache.commons.lang.StringUtils;

import javax.xml.namespace.QName;

public class KimCommonUtils {
    /**
     * Resolves the given kim type service name represented as a String to the appropriate QName.
     * If the value given is empty or null, then it will resolve to the default KimTypeService name.
     */
    public static QName resolveKimTypeServiceName(String kimTypeServiceName) {
        if (StringUtils.isBlank(kimTypeServiceName)) {
            return resolveKimTypeServiceName(KimConstants.DEFAULT_KIM_TYPE_SERVICE);
        }
        return QName.valueOf(kimTypeServiceName);
    }
}

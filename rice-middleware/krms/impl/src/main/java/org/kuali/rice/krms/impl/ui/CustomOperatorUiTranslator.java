package org.kuali.rice.krms.impl.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krms.api.repository.operator.CustomOperator;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;

import javax.xml.namespace.QName;

/**
 * Utility service used by the KRMS agenda editing UI for display and access to custom operators.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomOperatorUiTranslator {


    /**
     * Parses the {@link QName} for the custom operator service from the form value string, which has the format
     * {@code customOperator:<Namespace>:<serviceName>}
     *
     * @param customOperatorFormValue
     * @return the QName for the custom operator service
     */
    public QName parseCustomOperatorServiceQName(String customOperatorFormValue) {
        if (customOperatorFormValue == null ||
                !isCustomOperatorFormValue(customOperatorFormValue)) {
            throw new IllegalArgumentException("custom operator form value (" + customOperatorFormValue +
                    ") must be formatted as " +
                    KrmsImplConstants.CUSTOM_OPERATOR_PREFIX + "namespace:serviceName");
        }

        String [] customOperatorServiceInfo = customOperatorFormValue.split(":");

        if (customOperatorServiceInfo == null || customOperatorServiceInfo.length != 3) {
            throw new IllegalArgumentException("custom operator form value (" + customOperatorFormValue +
                    ") must be formatted as " +
                    KrmsImplConstants.CUSTOM_OPERATOR_PREFIX + "namespace:serviceName");
        }

        QName customOperatorServiceQName = new QName(customOperatorServiceInfo[1], customOperatorServiceInfo[2]);

        return customOperatorServiceQName;
    }

    /**
     * Gets the custom operator function name which is used for display purposes
     *
     * @param customOperatorFormValue the form value representing the custom operator
     * @return
     */
    public String getCustomOperatorName(String customOperatorFormValue) {
        return getCustomOperator(customOperatorFormValue).getOperatorFunctionDefinition().getName();
    }

    /**
     * Gets the service instance given the specially formatted form value.
     *
     * <p>The form value contains the namespace and name of the service, which is used internally for retrieval.</p>
     *
     * @param customOperatorFormValue the custom operator form value
     * @return the custom operator service instance
     * @throws IllegalArgumentException if the customOperatorFormValue is null or is formatted incorrectly
     */
    public CustomOperator getCustomOperator(String customOperatorFormValue) {
        return GlobalResourceLoader.getService(parseCustomOperatorServiceQName(customOperatorFormValue));
    }

    /**
     * Checks if a form value represents a custom operator.
     *
     * <p>The determination is made be checking for a special prefix value that is used by convention.</p>
     *
     * @param formValue the form value to check
     * @return true if the form value represents a custom operator.
     */
    public boolean isCustomOperatorFormValue(String formValue) {
        if (StringUtils.isEmpty(formValue)) {
            return false;
        }

        return formValue.startsWith(KrmsImplConstants.CUSTOM_OPERATOR_PREFIX);
    }

}

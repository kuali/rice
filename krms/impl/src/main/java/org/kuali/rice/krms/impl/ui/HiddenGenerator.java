package org.kuali.rice.krms.impl.ui;

import org.kuali.rice.krad.uif.field.GeneratedField;

/**
 * <p>Generator to output a hidden input with the name being the id of the configured {@link GeneratedField}</p>
 *
 * <p>example GeneratedField spring config:</p>
 * <pre>
 *       &lt;bean parent="GeneratedField" p:id="myHiddenFieldName"&gt;
 *         &lt;property name="renderingMethodInvoker"&gt;
 *           &lt;bean class="org.springframework.util.MethodInvoker" p:targetClass="org.kuali.rice.krms.impl.ui.HiddenGenerator" p:targetMethod="generate" /&gt;
 *         &lt;/property&gt;
 *       &lt;/bean&gt;</pre>
 */
public class HiddenGenerator {

    public static String generate(GeneratedField field) {
        return "<input type=\"hidden\" name=\""+field.getId()+"\" value=\"\"/>";
    }

}

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
package org.kuali.rice.krad.uif.component;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

/**
 * Extends <code>MethodInvoker</code> to add properties for specifying
 * a method for invocation within the UIF
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "methodConfig", parent = "Uif-MethodInvokerConfig")
public class MethodInvokerConfig extends MethodInvoker implements Serializable, Copyable {
    private static final long serialVersionUID = 6626790175367500081L;

    private String staticMethod;
    private Class[] argumentTypes;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws ClassNotFoundException, NoSuchMethodException {
        if ((getTargetObject() == null) && (getTargetClass() != null)) {
            try {
                setTargetObject(getTargetClass().newInstance());
            } catch (Exception e) {
                throw new RiceRuntimeException("Unable to create new intance of target class", e);
            }
        }

        super.prepare();
    }

    /**
     * Set a fully qualified static method name to invoke,
     * e.g. "example.MyExampleClass.myExampleMethod".
     * Convenient alternative to specifying targetClass and targetMethod.
     *
     * @return static method to invoke
     */
    @BeanTagAttribute
    public String getStaticMethod() {
        return staticMethod;
    }

    /**
     * Override to catch a set staticMethod since super does
     * not contain a getter
     *
     * @param staticMethod static method to invoke
     */
    @Override
    public void setStaticMethod(String staticMethod) {
        super.setStaticMethod(staticMethod);
        this.staticMethod = staticMethod;
    }

    /**
     * Declared argument types for the method to be invoked, if not set the types will
     * be retrieved based on the target class and target name
     *
     * @return method argument types
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.LISTVALUE)
    public Class[] getArgumentTypes() {
        if (argumentTypes == null) {
            return getMethodArgumentTypes();
        }

        return argumentTypes;
    }

    /**
     * Setter for the method argument types that should be invoked
     *
     * @param argumentTypes
     */
    public void setArgumentTypes(Class[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public Class getTargetClass() {
        return super.getTargetClass();
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public Object getTargetObject() {
        return super.getTargetObject();
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public String getTargetMethod() {
        return super.getTargetMethod();
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    @Override
    public Object[] getArguments() {
        return super.getArguments();
    }

    /**
     * Finds the method on the target class that matches the target name and
     * returns the declared parameter types
     *
     * @return method parameter types
     */
    protected Class[] getMethodArgumentTypes() {
        if (StringUtils.isNotBlank(staticMethod)) {
            int lastDotIndex = this.staticMethod.lastIndexOf('.');
            if (lastDotIndex == -1 || lastDotIndex == this.staticMethod.length()) {
                throw new IllegalArgumentException("staticMethod must be a fully qualified class plus method name: " +
                        "e.g. 'example.MyExampleClass.myExampleMethod'");
            }
            String className = this.staticMethod.substring(0, lastDotIndex);
            String methodName = this.staticMethod.substring(lastDotIndex + 1);
            try {
                setTargetClass(resolveClassName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to get class for name: " + className);
            }
            setTargetMethod(methodName);
        }

        Method matchingCandidate = findMatchingMethod();
        if (matchingCandidate != null) {
            return matchingCandidate.getParameterTypes();
        }

        Method[] candidates = ReflectionUtils.getAllDeclaredMethods(getTargetClass());
        for (Method candidate : candidates) {
            if (candidate.getName().equals(getTargetMethod())) {
                return candidate.getParameterTypes();
            }
        }

        return null;
    }

    /**
     * @see Copyable#clone()
     */
    @Override
    public MethodInvokerConfig clone() throws CloneNotSupportedException {
        return (MethodInvokerConfig) super.clone();
    }

}

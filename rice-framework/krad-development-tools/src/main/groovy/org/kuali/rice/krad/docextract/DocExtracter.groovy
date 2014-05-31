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
package org.kuali.rice.krad.docextract

import com.sun.javadoc.AnnotationDesc
import com.sun.javadoc.ClassDoc
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.ProgramElementDoc
import com.sun.javadoc.RootDoc
import com.sun.javadoc.Tag
import com.sun.tools.javadoc.Main
import org.apache.commons.lang.StringUtils
import org.kuali.rice.krad.datadictionary.parse.BeanTag
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute
import org.kuali.rice.krad.datadictionary.parse.BeanTags

import java.lang.reflect.Method

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class DocExtracter {
    static final GETTER_PREFIX = "get"
    static final GETTER_IS_PREFIX = "is"
    static final INHERIT_DOC_TAG = "{@inheritDoc}"
    static final PROPERTIES_FILE_NAME = "ComponentJavaDocs.properties"

    static String outputPath

    static String sourcePath
    static String sourcePackages
    static String excludePackages

    def static generateDocProperties(String outputPath, String sourcePath, String sourcePackages,
            String excludePackages) {
        DocExtracter.outputPath = outputPath
        DocExtracter.sourcePath = sourcePath
        DocExtracter.sourcePackages = sourcePackages
        DocExtracter.excludePackages = excludePackages

        Main.execute("-doclet", DocExtracter.class.name, "-sourcepath", DocExtracter.sourcePath, "-subpackages",
                DocExtracter.sourcePackages, "-exclude", DocExtracter.excludePackages)
    }

    def static boolean start(RootDoc doc) {
        writeDocsToPropertiesFile(doc)

        return true
    }

    def static writeDocsToPropertiesFile(RootDoc doc) {
        ClassDoc[] classes = doc.classes()
        SortedProperties prop = new SortedProperties()

        for (ClassDoc classDoc : classes) {
            // check only BeanTag classes
            if (!isAnnotatedWith(classDoc, BeanTag.class.name, BeanTags.class.name)) {
                continue
            }

            String className = classDoc.qualifiedName()
            String classDescription = classDoc.commentText()

            prop.setProperty(className, classDescription)

            MethodDoc[] methods = classDoc.methods()
            for (MethodDoc methodDoc : methods) {
                // check only getters that have BeanTagAttribute annotated
                if (!(methodDoc.parameters().length == 0 &&
                        (methodDoc.name().startsWith(GETTER_PREFIX) || methodDoc.name().startsWith(GETTER_IS_PREFIX)) &&
                        !(methodDoc.name().equals(GETTER_PREFIX) || methodDoc.name().equals(GETTER_IS_PREFIX)) &&
                        isAnnotatedWith(methodDoc, BeanTagAttribute.class.name))) {
                    continue
                }

                String methodName

                if (methodDoc.name().startsWith(GETTER_PREFIX)) {
                    methodName = methodDoc.name().replaceFirst(GETTER_PREFIX, "")
                } else {
                    methodName = methodDoc.name().replaceFirst(GETTER_IS_PREFIX, "")
                }

                String propertyName = Character.toLowerCase(methodName.charAt(0))
                propertyName = propertyName.concat(methodName.length() > 1 ? methodName.substring(1) : "")

                String propertyType = methodDoc.returnType().typeName()
                String propertyDescription = getMethodDocText(className, methodDoc, doc)

                prop.setProperty(className + "|" + propertyName + "|" + propertyType, propertyDescription)
            }
        }

        try {
            prop.store(new FileOutputStream(outputPath + "/" + PROPERTIES_FILE_NAME), null)
        } catch (IOException e) {
            throw new RuntimeException("Exception while storing documentation properties", e)
        }
    }

    def static String getMethodDocText(String className, MethodDoc methodDoc, RootDoc doc) {
        String methodDocText = ""

        if (methodDoc.commentText() != null && !methodDoc.commentText().isEmpty()) {
            methodDocText = methodDoc.commentText()
        }

        String rawDocText = methodDoc.getRawCommentText()
        if ((rawDocText == null) || !StringUtils.contains(rawDocText, INHERIT_DOC_TAG)) {
            return methodDocText
        }

        // check for override of superclass method first
        if (methodDoc.overriddenMethod() != null) {
            MethodDoc overridedMethodDoc = methodDoc.overriddenMethod()

            if ((overridedMethodDoc.commentText() != null) && !overridedMethodDoc.commentText().isEmpty()) {
                methodDocText = StringUtils.replace(methodDocText, INHERIT_DOC_TAG, overridedMethodDoc.commentText())
            }
        } else {
            // find implemented interface method
            Class<?> clazz = Class.forName(className)
            MethodDoc interfaceMethodDoc = findInterfaceMethodDoc(methodDoc.name(), clazz.getInterfaces(), doc)

            if ((interfaceMethodDoc != null) && (interfaceMethodDoc.commentText() != null) &&
                    !interfaceMethodDoc.commentText().isEmpty()) {
                methodDocText = StringUtils.replace(methodDocText, INHERIT_DOC_TAG, interfaceMethodDoc.commentText())
            }
        }

        return methodDocText
    }

    def static MethodDoc findInterfaceMethodDoc(String methodName, Class<?>[] interfaces, RootDoc doc) {
        for (Class<?> methodInterface : interfaces) {
            try {
                Method method = methodInterface.getMethod(methodName)
                if (method == null) {
                    continue
                }

                ClassDoc classDoc = doc.classNamed(method.getDeclaringClass().getName())

                return getNoParamMethodFromClassDoc(classDoc, methodName)
            } catch (NoSuchMethodException e) {
                // ignore and let loop continue checking for matching methods
            }
        }
    }

    def static MethodDoc getNoParamMethodFromClassDoc(ClassDoc classDoc, String methodName) {
        MethodDoc[] methods = classDoc.methods()

        for (MethodDoc methodDoc : methods) {
            if (methodDoc.name().equals(methodName) && methodDoc.parameters().length == 0) {
                return methodDoc
            }
        }

        return null
    }

    def static boolean isAnnotatedWith(ProgramElementDoc elementDoc, String... tagString) {
        AnnotationDesc[] annotations = elementDoc.annotations()

        for (AnnotationDesc annotation : annotations) {
            if (Arrays.asList(tagString).contains(annotation.annotationType().toString())) {
                return true
            }
        }

        return false
    }

    def static class SortedProperties extends Properties {
        public Enumeration keys() {
            Enumeration keysEnum = super.keys()
            Vector<String> keyList = new Vector<String>()

            while (keysEnum.hasMoreElements()) {
                keyList.add((String) keysEnum.nextElement())
            }

            Collections.sort(keyList)

            return keyList.elements()
        }
    }

}

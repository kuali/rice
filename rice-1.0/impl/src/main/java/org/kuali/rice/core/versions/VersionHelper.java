/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.core.versions;

import org.kuali.rice.core.versions.annotations.DeprecatedSince;
import org.kuali.rice.core.versions.annotations.SupportedSince;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionHelper {
    public static boolean areVersionsCompatible(Object source, Object target) {
        double sourceVer = getHighestVersion(source);
        double targetVer = getHighestVersion(target);
        return targetVer <= sourceVer;
    }

    public static boolean isVersionCompatible(Object obj, double ver) {
        return ver <= getHighestVersion(obj);
    }

    public static double getHighestVersion(Object obj) {
        // TODO: Could be more efficient using an Array Copy
        Class clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        Field[] fields = clazz.getFields();
        List<AnnotatedElement> elements;
        elements = new ArrayList<AnnotatedElement>(methods.length + fields.length + 1);
        elements.add(clazz);
        elements.addAll(Arrays.asList(methods));
        elements.addAll(Arrays.asList(fields));
        return getHighestVersion(elements);
    }

    protected static double getHighestVersion(List<AnnotatedElement> elements) {
        double highestVersion = 1.0;
        for ( AnnotatedElement element : elements ) {
            DeprecatedSince deprecated = element.getAnnotation(DeprecatedSince.class);
            SupportedSince supported = element.getAnnotation(SupportedSince.class);
            double compareVersion = getHighestVersion(
                    new Annotation[] {deprecated, supported});
            if ( highestVersion < compareVersion )
                highestVersion = compareVersion;
        }
        return highestVersion;
    }

    protected static double getHighestVersion(Annotation[] annotations) {
        double highestVersion = 1.0;
        for ( Annotation annotation : annotations ) {
            double compareVersion = 1.0;
            if ( annotation instanceof DeprecatedSince )
                compareVersion = ((DeprecatedSince)annotation).version();
            else if ( annotation instanceof DeprecatedSince )
                compareVersion = ((SupportedSince)annotation).version();
            if ( highestVersion < compareVersion )
                highestVersion = compareVersion;
        }
        return highestVersion;
    }

    public static boolean isMethodSupported(Object obj, String methodName,
                                            double ver) {
        Method[] methods = obj.getClass().getMethods();
        for ( Method method : methods ) {
            if ( method.getName().equals(methodName) ) {
                // TODO: Do this better, prefer methods with annotations
                //       to those without
                return isMethodSupported(method, ver);
            }
        }
        return false;
    }

    public static boolean isMethodSupported(Method method, double ver) {
        SupportedSince supported = method.getAnnotation(SupportedSince.class);
        DeprecatedSince deprecated = method.getAnnotation(DeprecatedSince.class);
        return isVersionValid(supported, deprecated, ver);
    }

    public static boolean isFieldSupported(Object obj, String fieldName,
                                           double ver) {
        try {
            Field field = obj.getClass().getField(fieldName);
            return isFieldSupported(field, ver);
        }
        catch ( NoSuchFieldException e ) {
            return false;
        }
    }

    public static boolean isFieldSupported(Field field, double ver) {
        SupportedSince supported = field.getAnnotation(SupportedSince.class);
        DeprecatedSince deprecated = field.getAnnotation(DeprecatedSince.class);
        return isVersionValid(supported, deprecated, ver);
    }

    public static boolean isClassSupported(Object obj, double ver) {
        SupportedSince supported = obj.getClass().getAnnotation(SupportedSince.class);
        DeprecatedSince deprecated = obj.getClass().getAnnotation(DeprecatedSince.class);
        return isVersionValid(supported, deprecated, ver);
    }

    protected static boolean isVersionValid(SupportedSince supported,
                                            DeprecatedSince deprecated,
                                            double ver) {
        double supportedVersion = supported != null ? supported.version() : 1.0;
        if ( supported == null || supported.version() <= ver )
            return ( deprecated == null || ver < deprecated.version() );
        else
            return false;
    }
}

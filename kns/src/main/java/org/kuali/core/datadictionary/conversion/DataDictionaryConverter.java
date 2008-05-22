/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.core.datadictionary.conversion;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.control.ApcSelectControlDefinition;
import org.kuali.core.datadictionary.control.CheckboxControlDefinition;
import org.kuali.core.datadictionary.control.CurrencyControlDefinition;
import org.kuali.core.datadictionary.control.HiddenControlDefinition;
import org.kuali.core.datadictionary.control.KualiUserControlDefinition;
import org.kuali.core.datadictionary.control.LookupHiddenControlDefinition;
import org.kuali.core.datadictionary.control.LookupReadonlyControlDefinition;
import org.kuali.core.datadictionary.control.RadioControlDefinition;
import org.kuali.core.datadictionary.control.SelectControlDefinition;
import org.kuali.core.datadictionary.control.TextControlDefinition;
import org.kuali.core.datadictionary.control.TextareaControlDefinition;
import org.kuali.core.datadictionary.control.WorkflowWorkgroupControlDefinition;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.datadictionary.mask.MaskFormatter;
import org.kuali.core.datadictionary.mask.MaskFormatterCustom;
import org.kuali.core.datadictionary.mask.MaskFormatterLiteral;
import org.kuali.core.datadictionary.mask.MaskFormatterSubString;
import org.kuali.core.datadictionary.validation.charlevel.AlphaNumericValidationPattern;
import org.kuali.core.datadictionary.validation.charlevel.AlphaValidationPattern;
import org.kuali.core.datadictionary.validation.charlevel.AnyCharacterValidationPattern;
import org.kuali.core.datadictionary.validation.charlevel.CharsetValidationPattern;
import org.kuali.core.datadictionary.validation.charlevel.NumericValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.DateValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.EmailAddressValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.FixedPointValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.FloatingPointValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.JavaClassValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.MonthValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.TimestampValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.YearValidationPattern;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.rice.util.ClassLoaderUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Assembles a DataDictionary from the contents of one or more specifed XML files or directories containing XML files.
 */
public class DataDictionaryConverter {
    // logger
    private static Log LOG = LogFactory.getLog(DataDictionaryConverter.class);

    private static final String PACKAGE_PREFIX = "/org/kuali/core/datadictionary/conversion/";

    private static final String BEAN_FILE_START = "<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + "    xmlns:p=\"http://www.springframework.org/schema/p\"\r\n" + "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans\r\n" + "        http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\">\r\n";

    private static final String BEAN_FILE_END = "\r\n</beans>\r\n";

    // DTD registration info
    private final static String[][] DTD_REGISTRATION_INFO = {{"-//Kuali Project//DTD Data Dictionary 1.0//EN", PACKAGE_PREFIX + "dataDictionary-1_0.dtd"},};

    private DataDictionary dataDictionary;

    private static final int INDENT_SIZE = 2;

    private static final boolean INCLUDE_NULL_PROPERTIES = false;

    private static String currentID = "";

    private static StringBuffer mainBean;
    private static StringBuffer attributeBeans;
    private static StringBuffer lookupBean;
    private static StringBuffer inquiryBean;
    private static StringBuffer maintenanceSectionBeans;
    private static StringBuffer workflowPropertiesBean;

    private static Map<Class, Object> defaultObjects = new HashMap<Class, Object>();

    private static Map<Class, String> templateBeanNames = new HashMap<Class, String>();
    static {
        templateBeanNames.put(ApcRuleDefinition.class, ApcRuleDefinition.class.getSimpleName());
        templateBeanNames.put(AttributeDefinition.class, AttributeDefinition.class.getSimpleName());
        templateBeanNames.put(AuthorizationDefinition.class, AuthorizationDefinition.class.getSimpleName());
        templateBeanNames.put(BusinessObjectEntry.class, BusinessObjectEntry.class.getSimpleName());
        templateBeanNames.put(CollectionDefinition.class, CollectionDefinition.class.getSimpleName());
        templateBeanNames.put(FieldDefinition.class, FieldDefinition.class.getSimpleName());
        templateBeanNames.put(FieldPairDefinition.class, FieldPairDefinition.class.getSimpleName());
        templateBeanNames.put(HeaderNavigation.class, HeaderNavigation.class.getSimpleName());
        templateBeanNames.put(HelpDefinition.class, HelpDefinition.class.getSimpleName());
        templateBeanNames.put(InquiryCollectionDefinition.class, InquiryCollectionDefinition.class.getSimpleName());
        templateBeanNames.put(InquiryDefinition.class, InquiryDefinition.class.getSimpleName());
        templateBeanNames.put(InquirySectionDefinition.class, InquirySectionDefinition.class.getSimpleName());
        templateBeanNames.put(InquirySubSectionHeaderDefinition.class, InquirySubSectionHeaderDefinition.class.getSimpleName());
        templateBeanNames.put(LookupDefinition.class, LookupDefinition.class.getSimpleName());
        templateBeanNames.put(MaintainableCollectionDefinition.class, MaintainableCollectionDefinition.class.getSimpleName());
        templateBeanNames.put(MaintainableFieldDefinition.class, MaintainableFieldDefinition.class.getSimpleName());
        templateBeanNames.put(MaintainableSectionDefinition.class, MaintainableSectionDefinition.class.getSimpleName());
        templateBeanNames.put(MaintainableSubSectionHeaderDefinition.class, MaintainableSubSectionHeaderDefinition.class.getSimpleName());
        templateBeanNames.put(MaintenanceDocumentEntry.class, MaintenanceDocumentEntry.class.getSimpleName());
        templateBeanNames.put(PrimitiveAttributeDefinition.class, PrimitiveAttributeDefinition.class.getSimpleName());
        templateBeanNames.put(ReferenceDefinition.class, ReferenceDefinition.class.getSimpleName());
        templateBeanNames.put(RelationshipDefinition.class, RelationshipDefinition.class.getSimpleName());
        templateBeanNames.put(SortDefinition.class, SortDefinition.class.getSimpleName());
        templateBeanNames.put(SupportAttributeDefinition.class, SupportAttributeDefinition.class.getSimpleName());
        templateBeanNames.put(TransactionalDocumentEntry.class, TransactionalDocumentEntry.class.getSimpleName());
        templateBeanNames.put(WorkflowProperties.class, WorkflowProperties.class.getSimpleName() );
        templateBeanNames.put(WorkflowPropertyGroup.class, WorkflowPropertyGroup.class.getSimpleName() );
        templateBeanNames.put(WorkflowProperty.class, WorkflowProperty.class.getSimpleName() );
        // TODO: control defs
        templateBeanNames.put(ApcSelectControlDefinition.class, ApcSelectControlDefinition.class.getSimpleName());
        templateBeanNames.put(CheckboxControlDefinition.class, CheckboxControlDefinition.class.getSimpleName());
        templateBeanNames.put(CurrencyControlDefinition.class, CurrencyControlDefinition.class.getSimpleName());
        templateBeanNames.put(HiddenControlDefinition.class, HiddenControlDefinition.class.getSimpleName());
        templateBeanNames.put(KualiUserControlDefinition.class, KualiUserControlDefinition.class.getSimpleName());
        templateBeanNames.put(LookupHiddenControlDefinition.class, LookupHiddenControlDefinition.class.getSimpleName());
        templateBeanNames.put(LookupReadonlyControlDefinition.class, LookupReadonlyControlDefinition.class.getSimpleName());
        templateBeanNames.put(RadioControlDefinition.class, RadioControlDefinition.class.getSimpleName());
        templateBeanNames.put(SelectControlDefinition.class, SelectControlDefinition.class.getSimpleName());
        templateBeanNames.put(TextareaControlDefinition.class, TextareaControlDefinition.class.getSimpleName());
        templateBeanNames.put(TextControlDefinition.class, TextControlDefinition.class.getSimpleName());
        templateBeanNames.put(WorkflowWorkgroupControlDefinition.class, WorkflowWorkgroupControlDefinition.class.getSimpleName());
        // TODO: validation beans
        templateBeanNames.put(Mask.class, Mask.class.getSimpleName());
        templateBeanNames.put(MaskFormatter.class, MaskFormatter.class.getSimpleName());
        templateBeanNames.put(MaskFormatterCustom.class, MaskFormatterCustom.class.getSimpleName());
        templateBeanNames.put(MaskFormatterLiteral.class, MaskFormatterLiteral.class.getSimpleName());
        templateBeanNames.put(MaskFormatterSubString.class, MaskFormatterSubString.class.getSimpleName());

        templateBeanNames.put(AlphaNumericValidationPattern.class, AlphaNumericValidationPattern.class.getSimpleName());
        templateBeanNames.put(AlphaValidationPattern.class, AlphaValidationPattern.class.getSimpleName());
        templateBeanNames.put(AnyCharacterValidationPattern.class, AnyCharacterValidationPattern.class.getSimpleName());
        templateBeanNames.put(CharsetValidationPattern.class, CharsetValidationPattern.class.getSimpleName());
        templateBeanNames.put(NumericValidationPattern.class, NumericValidationPattern.class.getSimpleName());
        templateBeanNames.put(DateValidationPattern.class, DateValidationPattern.class.getSimpleName());
        templateBeanNames.put(EmailAddressValidationPattern.class, EmailAddressValidationPattern.class.getSimpleName());
        templateBeanNames.put(FixedPointValidationPattern.class, FixedPointValidationPattern.class.getSimpleName());
        templateBeanNames.put(FloatingPointValidationPattern.class, FloatingPointValidationPattern.class.getSimpleName());
        templateBeanNames.put(JavaClassValidationPattern.class, JavaClassValidationPattern.class.getSimpleName());
        templateBeanNames.put(MonthValidationPattern.class, MonthValidationPattern.class.getSimpleName());
        templateBeanNames.put(PhoneNumberValidationPattern.class, PhoneNumberValidationPattern.class.getSimpleName());
        templateBeanNames.put(TimestampValidationPattern.class, TimestampValidationPattern.class.getSimpleName());
        templateBeanNames.put(YearValidationPattern.class, YearValidationPattern.class.getSimpleName());
        templateBeanNames.put(ZipcodeValidationPattern.class, ZipcodeValidationPattern.class.getSimpleName());

    }

//     public static void main(String[] args) throws Exception {
//        StringBuffer sb = new StringBuffer();
//        sb.append(BEAN_FILE_START);
//        for (Class beanClass : templateBeanNames.keySet()) {
//            sb.append("<bean id=\"").append(templateBeanNames.get(beanClass)).append("\" class=\"").append(beanClass.getName().replace(".conversion.", ".")).append("\" abstract=\"true\" />\r\n");
//        }
//        sb.append(BEAN_FILE_END);
//        System.out.println(sb);
//    }

    public static void main(String[] args) throws Exception {
        URL url = DataDictionaryConverter.class.getResource("files-to-convert.txt");
        System.out.println(url);
        // read file into list
        BufferedReader r = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
        String currLine = r.readLine();
        ArrayList<String> fileLocations = new ArrayList<String>();
        while (currLine != null) {
            fileLocations.add(currLine);
            currLine = r.readLine();
        }
        r.close();
        System.out.println(fileLocations);
        // loop over list, parsing all source files
        DataDictionaryConverter converter = new DataDictionaryConverter();

        converter.convertDDFiles(fileLocations);
    }

    void convertDDFiles(List<String> fileLocations) throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        // load all DD files specified into the DD
        for (String location : fileLocations) {
            File file = resourceLoader.getResource(location).getFile();
            if (file.getName().equals("DataDictionaryBaseTypes.xml"))
                continue; // skip the base definitions file
            // if ( file.getName().equals("conversion") ) continue; // skip the conversion directory
            parseFilesAtLocation(file);
        }
        // for ( BusinessObjectEntry entry : dataDictionary.getBusinessObjectEntries().values() ) {
        // System.out.println( entry.getFullClassName() + " : " + entry.sourceFile );
        // }
        // update all attribute references
        for (DataDictionaryEntryBase entry : dataDictionary.getAllEntries()) {
            entry.expandAttributeReferences(dataDictionary);
        }

        for (DataDictionaryEntryBase entry : dataDictionary.getAllEntries()) {
            // backup the original file
            File originalFile = new File(entry.sourceFile);
            File backupDir = new File(originalFile.getParentFile(), "backup");
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            File backupFile = new File(backupDir, originalFile.getName());
            if (!backupFile.exists()) {
                copyFile(originalFile, backupFile);
            }
            mainBean = new StringBuffer(20000);
            attributeBeans = new StringBuffer(20000);
            lookupBean = new StringBuffer(10000);
            inquiryBean = new StringBuffer(10000);
            maintenanceSectionBeans = new StringBuffer(10000);
            workflowPropertiesBean = new StringBuffer(10000);
            mainBean.append(BEAN_FILE_START);
            currentID = entry.getJstlKey();
            exportBean(mainBean, entry, currentID, INDENT_SIZE);

            // export other bean StringBuffers
            if (attributeBeans.length() > 0) {
                mainBean.append("\r\n\r\n");
                mainBean.append("<!-- Attribute Definitions -->");
                mainBean.append("\r\n\r\n");
                mainBean.append(attributeBeans);
            }

            if (maintenanceSectionBeans.length() > 0) {
                mainBean.append("\r\n\r\n");
                mainBean.append("<!-- Maintenance Section Definitions -->");
                mainBean.append("\r\n\r\n");
                mainBean.append(maintenanceSectionBeans);
            }
            
            if (inquiryBean.length() > 0) {
                mainBean.append("\r\n\r\n");
                mainBean.append("<!-- Business Object Inquiry Definition -->");
                mainBean.append("\r\n\r\n");
                mainBean.append(inquiryBean);
            }

            if (lookupBean.length() > 0) {
                mainBean.append("\r\n\r\n");
                mainBean.append("<!-- Business Object Lookup Definition -->");
                mainBean.append("\r\n\r\n");
                mainBean.append(lookupBean);
            }

            if (workflowPropertiesBean.length() > 0) {
                mainBean.append("\r\n\r\n");
                mainBean.append("<!-- Exported Workflow Properties -->");
                mainBean.append("\r\n\r\n");
                mainBean.append(workflowPropertiesBean);
            }
            
            mainBean.append(BEAN_FILE_END);
            // save the string to a file
            BufferedWriter out = new BufferedWriter(new FileWriter(originalFile));
            out.write(mainBean.toString());
            out.close();
        }
        // now, copy *.xml files back to work/src from work/web-root/web-inf/classes - KFS specific
        // copyDirectory(baseClassesDir, baseSourceDir);

    }

    private void newLineAndIndent(StringBuffer sb, int indent) {
        sb.append("\r\n");
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
    }

    private boolean isSpecialPropertyToIgnore(Class beanClass, String propertyName) {
        return propertyName.equals("signers") 
                || propertyName.equals("protectionDomain0") 
                || propertyName.equals("collectionNames") 
                || propertyName.equals("jstlKey") 
                || propertyName.equals("fullClassName") 
                || propertyName.equals("delegate") 
                || (beanClass.equals(AttributeReferenceDefinition.class) && propertyName.equals("sourceClassName")) 
                || (beanClass.equals(AttributeReferenceDefinition.class) && propertyName.equals("sourceAttributeName")) 
                || propertyName.equals("class") 
                || propertyName.equals("annotationType")
                || (beanClass.equals(InquiryCollectionDefinition.class) && propertyName.equals("name"))
                || (beanClass.equals(InquirySubSectionHeaderDefinition.class) && propertyName.equals("name"))
         ;
    }

    private boolean areAllPropertiesSimple(Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterTypes().length == 0) {
                // LOG.error( "checking Method: " + bean.getClass().getSimpleName() + "." + method.getName());
                String propertyName = "";
                if (method.getName().startsWith("get")) {
                    propertyName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                } else {
                    propertyName = method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
                }
                if (isSpecialPropertyToIgnore(bean.getClass(), propertyName)) {
                    continue;
                }
                Object propertyValue = null;
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
                    propertyValue = pd.getReadMethod().invoke(bean, (Object[]) null);
                    if ( pd.getWriteMethod() == null ) continue;
                } catch (IntrospectionException ex) {
                    LOG.warn("Unable to get descriptor for property: " + bean.getClass().getName() + "." + propertyName + "(" + ex.getMessage() + ")");
                    continue;
                } catch (Exception ex) {
                    LOG.warn("Error getting property value: " + ex.getMessage());
                    continue;
                }
                if (propertyValue != null && !isSimpleType(propertyValue.getClass())) {
                    LOG.debug("Found non-simple property: " + bean.getClass().getName() + "." + propertyName);
                    return false;
                }
            }
        }
        return true;
    }

    private void exportBean(StringBuffer sb, Object bean, String id, int indent) throws Exception {
        String beanID = currentID;
        if (bean instanceof AttributeDefinition) {
            // add bean reference to current stringbuffer
            newLineAndIndent(sb, indent);
            beanID = currentID + "-" + ((AttributeDefinition) bean).getName();
            sb.append("<ref bean=\"").append(beanID).append("\" />");
            // set buffer to the attributes buffer (and reset the indent)
            sb = attributeBeans;
            indent = INDENT_SIZE;
        } else if (bean instanceof LookupDefinition) {
            // add bean reference to current stringbuffer
            newLineAndIndent(sb, indent);
            beanID = currentID + "-lookupDefinition";
            sb.append("<ref bean=\"").append(beanID).append("\" />");
            // set buffer to the attributes buffer (and reset the indent)
            sb = lookupBean;
            indent = INDENT_SIZE;
        } else if (bean instanceof InquiryDefinition) {
            // add bean reference to current stringbuffer
            newLineAndIndent(sb, indent);
            beanID = currentID + "-inquiryDefinition";
            sb.append("<ref bean=\"").append(beanID).append("\" />");
            // set buffer to the attributes buffer (and reset the indent)
            sb = inquiryBean;
            indent = INDENT_SIZE;
        } else if (bean instanceof MaintainableSectionDefinition) {
            // add bean reference to current stringbuffer
            newLineAndIndent(sb, indent);
            beanID = currentID + "-" + ((MaintainableSectionDefinition)bean).getTitle().replace(" ", "");
            sb.append("<ref bean=\"").append(beanID).append("\" />");
            // set buffer to the attributes buffer (and reset the indent)
            sb = maintenanceSectionBeans;
            indent = INDENT_SIZE;
        } else if (bean instanceof WorkflowProperties) {
            // add bean reference to current stringbuffer
            newLineAndIndent(sb, indent);
            beanID = currentID + "-workflowProperties";
            sb.append("<ref bean=\"").append(beanID).append("\" />");
            // set buffer to the attributes buffer (and reset the indent)
            sb = workflowPropertiesBean;
            indent = INDENT_SIZE;
        } else {
            if ( StringUtils.isNotBlank(id) ) {
                beanID = id;
            } else {
                beanID = null;
            }
        }
        newLineAndIndent(sb, indent);
        boolean useSimplePropertyFormat = false;
        if (areAllPropertiesSimple(bean)) {
            useSimplePropertyFormat = true;
        }
        boolean parentUsed = false;
        sb.append("<bean ");
        if ( beanID != null ) {
            // create a parent bean for extending
            // <bean id="Chart" parent="Chart-parentBean" />
            sb.append("id=\"").append(beanID).append("\" parent=\"").append(beanID).append("-parentBean").append("\" />\r\n");
            // <bean id="Chart-parentBean" abstract="true"
            newLineAndIndent(sb, indent);
            sb.append("<bean ");
            sb.append("id=\"").append(beanID).append("-parentBean").append("\" abstract=\"true\" ");
        }
        if (!(bean instanceof AttributeReferenceDefinition)) {
            if (!parentUsed) {
                if (templateBeanNames.containsKey(bean.getClass())) {
                    sb.append("parent=\"").append(templateBeanNames.get(bean.getClass())).append("\"");
                } else {
                    sb.append("class=\"").append(bean.getClass().getName().replace(".conversion.", ".")).append("\"");
                }
            }
        } else {
            sb.append("parent=\"").append(StringUtils.substringAfterLast(((AttributeReferenceDefinition) bean).getSourceClassName(), ".")).append('-').append(((AttributeReferenceDefinition) bean).getSourceAttributeName()).append("\"");
            parentUsed = true;
        }
        if (!useSimplePropertyFormat) {
            sb.append(">");
        }
        Set<String> extractedProperties = new HashSet<String>();
        for (Method method : bean.getClass().getMethods()) {
            if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterTypes().length == 0) {
                // LOG.error( "checking Method: " + bean.getClass().getSimpleName() + "." + method.getName());
                String propertyName = "";
                if (method.getName().startsWith("get")) {
                    propertyName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                } else {
                    propertyName = method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
                }
                if (isSpecialPropertyToIgnore(bean.getClass(),propertyName)) {
                    continue;
                }
                if ( extractedProperties.contains(propertyName) ) {
                    continue;
                }
                extractedProperties.add(propertyName);

                Object propertyValue = null;
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
                    propertyValue = pd.getReadMethod().invoke(bean, (Object[]) null);
                    // check for setter
                    if ( pd.getWriteMethod() == null ) continue;
                } catch (IntrospectionException ex) {
                    LOG.error("Unable to get descriptor for property: " + bean.getClass().getName() + "." + propertyName + "(" + ex.getMessage() + ")");
                    continue;
                } catch (Exception ex) {
                    LOG.error("Error getting property value: " + ex.getMessage());
                    continue;
                }
                boolean exportProperty = true;
                if (bean instanceof AttributeReferenceDefinition) {
                    Object delegate = ((AttributeReferenceDefinition) bean).getDelegate();
                    String delegateProperty = propertyName;
                    try {
                        if (delegate != null) {
                            PropertyDescriptor pd = new PropertyDescriptor(delegateProperty, delegate.getClass());
                            Object delegatePropertyValue = pd.getReadMethod().invoke(delegate, (Object[]) null);
                            if (ObjectUtils.equals(delegatePropertyValue, propertyValue)) {
                                exportProperty = false;
                            }
                        }
                    } catch (IntrospectionException ex) {
                        LOG.error("Unable to get descriptor for property on attribute reference: " + delegate.getClass().getName() + "." + delegateProperty + "(" + ex.getMessage() + ")");
                    }
                } else {
                    // check if value is the default for the object. If so, do not export.
                    try {
                        Object defaultObject = defaultObjects.get(bean.getClass());
                        if (defaultObject == null) {
                            defaultObject = bean.getClass().newInstance();
                            defaultObjects.put(bean.getClass(), defaultObject);
                        }
                        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
                        Object defaultValue = pd.getReadMethod().invoke(defaultObject, (Object[]) null);
                        if (ObjectUtils.equals(defaultValue, propertyValue)) {
                            exportProperty = false;
                        }
                    } catch (Exception ex) {
                        // do nothing
                        LOG.error("Unable to compare property to default: " + bean.getClass().getName() + "." + propertyName + "(" + ex.getMessage() + ")");
                    }
                }
                if (exportProperty) {
                    if (useSimplePropertyFormat) {
                        if (propertyValue != null) {
                            newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE + INDENT_SIZE);
                            sb.append("p:").append(propertyName).append("=\"");
                            sb.append(getSimpleValue(propertyValue)).append("\"");
                        }
                    } else {
                        addProperty(sb, propertyName, propertyValue, indent + INDENT_SIZE);
                    }
                }
            }
        }

        if (useSimplePropertyFormat) {
            sb.append(" />");
        } else {
            newLineAndIndent(sb, indent);
            sb.append("</bean>");
        }
    }

    private boolean isSimpleType(Class clazz) {
        return clazz == String.class || clazz == Boolean.class || clazz == Character.class || clazz == Class.class || clazz.isPrimitive() || Number.class.isAssignableFrom(clazz);
    }

    private String getSimpleValue(Object value) {
        if (value.getClass() == Class.class) {
            return ((Class) value).getName();
        } else {
            return xmlEscape(value.toString());
        }
    }

    private String xmlEscape(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;");
    }

    private void exportSet(StringBuffer sb, Set value, int indent) throws Exception {
        newLineAndIndent(sb, indent);
        sb.append("<set>");
        for (Object listEntry : value) {
            exportObject(sb, listEntry, indent + INDENT_SIZE);
        }
        newLineAndIndent(sb, indent);
        sb.append("</set>");
    }

    private void exportList(StringBuffer sb, List value, int indent) throws Exception {
        newLineAndIndent(sb, indent);
        sb.append("<list>");
        for (Object listEntry : value) {
            exportObject(sb, listEntry, indent + INDENT_SIZE);
        }
        newLineAndIndent(sb, indent);
        sb.append("</list>");
    }

    private void exportObject(StringBuffer sb, Object obj, int indent) throws Exception {
        if (obj == null) {
            newLineAndIndent(sb, indent);
            sb.append("<null />");
        } else if (isSimpleType(obj.getClass())) {
            newLineAndIndent(sb, indent);
            sb.append("<value>").append(getSimpleValue(obj)).append("</value>");
        } else if (Set.class.isAssignableFrom(obj.getClass())) {
            exportSet(sb, (Set) obj, indent);
        } else if (List.class.isAssignableFrom(obj.getClass())) {
            exportList(sb, (List) obj, indent);
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            exportMap(sb, (Map) obj, indent);
        } else {
            exportBean(sb, obj, null, indent);
        }
    }

    private void exportMap(StringBuffer sb, Map map, int indent) throws Exception {
        newLineAndIndent(sb, indent);
        sb.append("<map>");
        for (Map.Entry mapEntry : (Set<Map.Entry>) map.entrySet()) {
            newLineAndIndent(sb, indent + INDENT_SIZE);
            sb.append("<entry>");
            if (mapEntry.getKey() != null && isSimpleType(mapEntry.getKey().getClass())) {
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("<key value=\"").append(getSimpleValue(mapEntry.getKey())).append("\" />");
            } else {
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("<key>");
                exportObject(sb, mapEntry.getKey(), indent + INDENT_SIZE + INDENT_SIZE + INDENT_SIZE);
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("</key>");
            }
            if (mapEntry.getValue() != null && isSimpleType(mapEntry.getValue().getClass())) {
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("<value>").append(getSimpleValue(mapEntry.getValue())).append("</value>");
            } else {
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("<value>");
                exportObject(sb, mapEntry.getValue(), indent + INDENT_SIZE + INDENT_SIZE + INDENT_SIZE);
                newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
                sb.append("</value>");
            }
            newLineAndIndent(sb, indent + INDENT_SIZE + INDENT_SIZE);
            sb.append("</entry>");
        }
        newLineAndIndent(sb, indent);
        sb.append("</map>");
    }

    private void addProperty(StringBuffer sb, String propertyName, Object value, int indent) throws Exception {
        if (value != null || INCLUDE_NULL_PROPERTIES) {
            newLineAndIndent(sb, indent);
            sb.append("<property name=\"").append(propertyName).append("\" ");
            if (value == null) {
                sb.append("><null /></property>");
            } else if (isSimpleType(value.getClass())) {
                sb.append("value=\"").append(getSimpleValue(value)).append("\" />");
            } else {
                sb.append(">");
                exportObject(sb, value, indent + INDENT_SIZE);
                newLineAndIndent(sb, indent);
                sb.append("</property>");
            }
        }
    }

    public void copyFile(File in, File out) throws Exception {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {

            File[] children = sourceLocation.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory() 
                            && !pathname.getName().equals("backup")
                            // && !pathname.getName().equals( "conversion" )
                            && !pathname.getName().equals( "CVS" )
                    ) {
                        return true;
                    }
                    if (pathname.getName().endsWith(".xml") 
                            && !pathname.getName().contains("SpringBeans") 
                            && !pathname.getName().startsWith("OJB") 
                            && !pathname.getName().startsWith("Spring") 
                            && !pathname.getName().startsWith("dwr") 
                            && !pathname.getName().startsWith("DigesterRules")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File child : children) {
                copyDirectory(new File(sourceLocation, child.getName()), new File(targetLocation, child.getName()));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private void parseFilesAtLocation(File loc) {
        if (loc.getName().endsWith(".xml")) {
            System.out.println("Parsing File: " + loc.getName());
            addEntries(loc);
        } else if (loc.isDirectory()) { // treat as directory
            System.out.println("Entering Directory: " + loc.getAbsolutePath());
            File[] dirFiles = loc.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isDirectory() 
                            && !pathname.getName().equals("backup")
                            // && !pathname.getName().equals( "conversion" )
                            && !pathname.getName().equals( "CVS" )
                    ) {
                        return true;
                    }
                    if (pathname.getName().endsWith(".xml") 
                            && !pathname.getName().contains("SpringBeans") 
                            && !pathname.getName().startsWith("OJB") 
                            && !pathname.getName().startsWith("Spring") 
                            && !pathname.getName().startsWith("dwr") 
                            && !pathname.getName().startsWith("DigesterRules")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : dirFiles) {
                parseFilesAtLocation(file);
            }
        }
    }

    /**
     * Default constructor
     */
    public DataDictionaryConverter() {
        dataDictionary = new DataDictionary();
    }

    private ThreadLocal<Rules> digesterRules = new ThreadLocal<Rules>();

    protected synchronized void addEntries(File source) {
        // ensure a separate copy of the digester rules per accessing thread
        if (digesterRules.get() == null) {
            digesterRules.set(loadRules());
        }
        Digester digester = buildDigester(digesterRules.get());

        try {
            digest(source, digester);
        } catch (Exception e) {
            throw new DataDictionaryException("Problems parsing DD for sourceName: " + source.getName(), e);
        } finally {
            if (digester != null) {
                digester.clear();
            }
        }

        clearCurrentDigester();
        clearCurrentFilename();
    }

    protected void setupDigester(Digester digester) {
        setCurrentDigester(digester);
        digester.push(dataDictionary);
    }

    protected void digest(File file, Digester digester) throws IOException, SAXException {
        setupDigester(digester);
        digester.setErrorHandler(new XmlErrorHandler(file.getName()));
        setCurrentFilename(file.getAbsolutePath());
        digester.parse(file);
    }

    /**
     * @return Rules loaded from the appropriate XML file
     */
    protected Rules loadRules() {
        // locate Digester rules
        URL rulesUrl = getClass().getResource(PACKAGE_PREFIX + "digesterRules.xml");
        System.out.println( "Digester File: " + rulesUrl );
        if (rulesUrl == null) {
            throw new InitException("unable to locate digester rules file");
        }

        // create and init digester
        Digester digester = DigesterLoader.createDigester(rulesUrl);

        return digester.getRules();
    }

    /**
     * @return fully-initialized Digester used to process entry XML files
     */
    protected Digester buildDigester(Rules rules) {
        Digester digester = new Digester();
        digester.setNamespaceAware(false);
        digester.setValidating(true);

        // register DTD(s)
        for (int i = 0; i < DTD_REGISTRATION_INFO.length; ++i) {
            String dtdPublic = DTD_REGISTRATION_INFO[i][0];
            String dtdPath = DTD_REGISTRATION_INFO[i][1];

            URL dtdUrl = getClass().getResource(dtdPath);
            if (dtdUrl == null) {
                throw new InitException("unable to locate DTD at \"" + dtdPath + "\"");
            }
            digester.register(dtdPublic, dtdUrl.toString());
        }

        digester.setRules(rules);

        return digester;
    }

    /**
     * This is a rather ugly hack which expose the Digester being used to parse a given XML file so that error messages
     * generated during parsing can contain file and line number info.
     * <p>
     * If we weren't using an XML file to configure Digester, I'd do this by rewriting all of the rules so that they accepted
     * the Digester instance as a param, which would be considerably less ugly.
     */

    /**
     * @return name of the XML file currently being parsed
     * @throws IllegalStateException
     *             if parsing is not in progress
     */
    public static String getCurrentFileName() {
        // try to prevent invalid access to nonexistent filename
        if (currentFilename == null) {
            throw new IllegalStateException("current filename is null");
        }

        return currentFilename;
        // return "";
    }

    /**
     * @return line number in the XML file currently being parsed
     * @throws IllegalStateException
     *             if parsing is not in progress
     */
    public static int getCurrentLineNumber() {
        Locator locator = getCurrentDigester().getDocumentLocator();
        if (locator != null) {
            return locator.getLineNumber();
        } else {
            return 0;
        }
    }

    private static Digester currentDigester;

    protected static void setCurrentDigester(Digester newDigester) {
        currentDigester = newDigester;
    }

    protected static void clearCurrentDigester() {
        currentDigester = null;
    }

    protected static Digester getCurrentDigester() {
        // // try to prevent invalid access to nonexistent digester instance
        if (currentDigester == null) {
            throw new IllegalStateException("current digester is null");
        }

        return currentDigester;
    }

    private static String currentFilename;

    protected void setCurrentFilename(String newFilename) {
        currentFilename = newFilename;
    }

    protected void clearCurrentFilename() {
        currentFilename = null;
    }


}
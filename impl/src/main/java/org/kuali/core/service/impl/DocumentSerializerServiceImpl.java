/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.core.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentSerializerService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.XmlObjectSerializerService;
import org.kuali.core.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.core.util.documentserializer.DocumentSerializationState;
import org.kuali.core.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.core.util.documentserializer.PropertyType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Default implementation of the {@link DocumentSerializerService}.  If no &lt;workflowProperties&gt; have been defined in the
 * data dictionary for a document type (i.e. {@link Document#getDocumentPropertySerizabilityEvaluator()} returns an instance of 
 * {@link AlwaysTruePropertySerializibilityEvaluator}), then this service will revert to using the {@link XmlObjectSerializerService}
 * bean, which was the old way of serializing a document for routing.  If workflowProperties are defined, then this implementation
 * will selectively serialize items.
 */
public class DocumentSerializerServiceImpl implements DocumentSerializerService {
    private static final Log LOG = LogFactory.getLog(DocumentSerializerServiceImpl.class);
    
    private PersistenceService persistenceService;
    private XmlObjectSerializerService xmlObjectSerializerService;
    
    private XStream xstream;
    private ThreadLocal<DocumentSerializationState> serializationStates;
    private ThreadLocal<PropertySerializabilityEvaluator> evaluators;
    
    public DocumentSerializerServiceImpl() {
        serializationStates = new ThreadLocal<DocumentSerializationState>();
        evaluators = new ThreadLocal<PropertySerializabilityEvaluator>();
        
        xstream = new XStream(new ProxyAndStateAwareJavaReflectionProvider());
        xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
        xstream.registerConverter(new UniversalUserConverter(xstream.getMapper(), new UniversalUserReflectionProvider()));
    }
    
    /**
     * Serializes a document for routing
     * 
     * @see org.kuali.core.service.DocumentSerializerService#serializeDocumentToXmlForRouting(org.kuali.core.document.Document)
     */
    public String serializeDocumentToXmlForRouting(Document document) {
        PropertySerializabilityEvaluator propertySerizabilityEvaluator = document.getDocumentPropertySerizabilityEvaluator();
        evaluators.set(propertySerizabilityEvaluator);
        DocumentSerializationState state = createNewDocumentSerializationState(document);
        serializationStates.set(state);
        
        Object xmlWrapper = wrapDocumentWithMetadata(document);
        String xml;
        if (propertySerizabilityEvaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
            xml = getXmlObjectSerializerService().toXml(xmlWrapper);
        }
        else {
            xml = xstream.toXML(xmlWrapper);
        }
        
        evaluators.set(null);
        serializationStates.set(null);
        return xml;
    }

    /**
     * Wraps the document before it is routed.  This implementation defers to {@link Document#wrapDocumentWithMetadataForXmlSerialization()}.
     * 
     * @param document
     * @return may return the document, or may return another object that wraps around the document to provide additional metadata
     */
    protected Object wrapDocumentWithMetadata(Document document) {
        return document.wrapDocumentWithMetadataForXmlSerialization();
    }
    
    public class ProxyConverter extends ReflectionConverter {
        public ProxyConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
        public boolean canConvert(Class clazz) {
            return clazz.getName().indexOf("CGLIB") > -1 || clazz.getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
        }

        public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
            if (obj instanceof ListProxyDefaultImpl) { 
                List copiedList = new ArrayList(); 
                List proxiedList = (List) obj; 
                for (Iterator iter = proxiedList.iterator(); iter.hasNext();) { 
                    copiedList.add(iter.next()); 
                } 
                context.convertAnother( copiedList );
            } 
            else { 
                super.marshal(getPersistenceService().resolveProxy(obj), writer, context);
            }           
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }
    }
    
    public class ProxyAndStateAwareJavaReflectionProvider extends PureJavaReflectionProvider {
        @Override
        public void visitSerializableFields(Object object, Visitor visitor) {
            DocumentSerializationState state = serializationStates.get();
            PropertySerializabilityEvaluator evaluator = evaluators.get();
            
            for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }
                
                if (ignoreField(field)) {
                    continue;
                }
                
                validateFieldAccess(field);
                
                initializeField(object, field);
                
                Object value = null;
                try {
                    value = field.get(object);
                } catch (IllegalArgumentException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                }
                
                if (evaluator.isPropertySerializable(state, object, field.getName(), value)) {
                    if (value != null && ProxyHelper.isProxy(value)) {
                        // resolve proxies after we determine that it's serializable
                        value = getPersistenceService().resolveProxy(value);
                    }
                    PropertyType propertyType = evaluator.determinePropertyType(value);
                    state.addSerializedProperty(field.getName(), propertyType);
                    visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
                    state.removeSerializedProperty();
                }
            }
        }
        
        protected boolean ignoreField(Field field) {
            return false;
        }
        
        protected void initializeField(Object object, Field field) {
        }
    }

    public class UniversalUserConverter extends ReflectionConverter {
        private UniversalUserReflectionProvider reflectionProvider;
        
        public UniversalUserConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
            this.reflectionProvider = (UniversalUserReflectionProvider) reflectionProvider;
        }
        
        @Override
        public boolean canConvert(Class type) {
            return UniversalUser.class.isAssignableFrom(type);
        }
    }
    
    public class UniversalUserReflectionProvider extends ProxyAndStateAwareJavaReflectionProvider {
        private Set<String> primitivePropertiesPopulatedByServices;
        private PropertyUtilsBean propertyUtilsBean;
        
        public UniversalUserReflectionProvider() {
            initializePrimitivePropertiesPopulatedByServices();
            propertyUtilsBean = new PropertyUtilsBean();
        }
        
        @Override
        protected void initializeField(Object object, Field field) {
            UniversalUser user = (UniversalUser) object;
            String fieldName = field.getName();
            
            if (primitivePropertiesPopulatedByServices.contains(fieldName)) {
                try {
                    // some universal user properties are initialized by calling the getter method for the appropriate method
                    // so, here, we use property utils bean/introspection to call the getter method, so that it will be populated
                    // when the reflection provider attempts to access these properties
                    propertyUtilsBean.getProperty(user, fieldName);
                } catch (Exception e) {
                    LOG.error("Cannot use getter method for UniversalUser property: " + fieldName, e);
                }
            }
        }

        protected void initializePrimitivePropertiesPopulatedByServices() {
            primitivePropertiesPopulatedByServices = new HashSet<String>();
            primitivePropertiesPopulatedByServices.add("moduleProperties");
            primitivePropertiesPopulatedByServices.add("moduleUsers");
        }
    }
    
    public PersistenceService getPersistenceService() {
        return this.persistenceService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
    
    public XmlObjectSerializerService getXmlObjectSerializerService() {
        return this.xmlObjectSerializerService;
    }

    public void setXmlObjectSerializerService(XmlObjectSerializerService xmlObjectSerializerService) {
        this.xmlObjectSerializerService = xmlObjectSerializerService;
    }
    
    protected DocumentSerializationState createNewDocumentSerializationState(Document document) {
        return new DocumentSerializationState();
    }
}

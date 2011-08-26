/*
 * Copyright 2005-2007 The Kuali Foundation
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

package org.kuali.rice.krad.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.apache.ojb.broker.core.proxy.SetProxyDefaultImpl;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.XmlObjectSerializerService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class is the service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework.
 * 
 * 
 */
public class XmlObjectSerializerServiceImpl implements XmlObjectSerializerService {
	private static final Log LOG = LogFactory.getLog(XmlObjectSerializerServiceImpl.class);

	private PersistenceService persistenceService;
	
	private XStream xstream;
	
	public XmlObjectSerializerServiceImpl() {
		xstream = new XStream(new ProxyAwareJavaReflectionProvider());
		xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));

        // register converters so that ListProxyDefaultImpl and SetProxyDefaultImpl are
        // serialized as ArrayLists and HashSets
        xstream.registerConverter(new ListProxyDefaultImplConverter(xstream.getMapper()));
        xstream.registerConverter(new SetProxyDefaultImplConverter(xstream.getMapper()));
	}
	
    /**
     * @see org.kuali.rice.krad.service.XmlObjectSerializerService#toXml(java.lang.Object)
     */
    public String toXml(Object object) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "toXml(" + object + ") : \n" + xstream.toXML(object) );
    	}
        return xstream.toXML(object);
    }

    /**
     * @see org.kuali.rice.krad.service.XmlObjectSerializerService#fromXml(java.lang.String)
     */
    public Object fromXml(String xml) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "fromXml() : \n" + xml );
    	}
    	if ( xml != null ) {
    		xml = xml.replaceAll( "--EnhancerByCGLIB--[0-9a-f]{0,8}", "" );
    	}
        return xstream.fromXML(xml);
    }

    /**
     * This custom converter only handles proxies for BusinessObjects.  List-type proxies are handled by configuring XStream to treat
     * ListProxyDefaultImpl as ArrayLists (see constructor for this service). 
     */
    public class ProxyConverter extends ReflectionConverter {
        public ProxyConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
        
        @Override
        // since the ReflectionConverter supertype defines canConvert without using a parameterized Class type, we must declare
        // the overridden version the same way
        @SuppressWarnings("unchecked")
        public boolean canConvert(Class clazz) {
            return clazz.getName().contains("CGLIB");
        }

        @Override
        public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
            super.marshal(getPersistenceService().resolveProxy(obj), writer, context);
        }
        
        // we shouldn't need an unmarshal method because all proxy metadata is taken out of the XML, so we'll reserialize as a base BO. 
    }
    
    public class ProxyAwareJavaReflectionProvider extends PureJavaReflectionProvider {

    	public ProxyAwareJavaReflectionProvider() {
    		super();
    	}
        /**
         * @see com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider#visitSerializableFields(java.lang.Object, com.thoughtworks.xstream.converters.reflection.ReflectionProvider.Visitor)
         */
        @Override
        public void visitSerializableFields(Object object, Visitor visitor) {
            for (Iterator iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }
                validateFieldAccess(field);
                Object value = null;
                try {
                    value = field.get(object);
                    if (value != null && getPersistenceService().isProxied(value)) {
                        value = getPersistenceService().resolveProxy(value);
                    }
                } catch (IllegalArgumentException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                }
                visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
            }
        }

    }

	public PersistenceService getPersistenceService() {
		if ( persistenceService == null ) {
			persistenceService = KRADServiceLocator.getPersistenceService();
		}
		return persistenceService;
	}

    /**
     * Custom {@link com.thoughtworks.xstream.converters.Converter} that moves elements from a
     * {@link ListProxyDefaultImpl} into an {@link ArrayList} and marshals that instead.
     */
    private static class ListProxyDefaultImplConverter extends CollectionConverter {

        ListProxyDefaultImplConverter(Mapper mapper) {
            super(mapper);
        }

        @Override @SuppressWarnings("unchecked")
        public boolean canConvert(Class type) {
            return ListProxyDefaultImpl.class.equals(type);
        }

        /**
         * moves elements from a {@link ListProxyDefaultImpl} into an {@link ArrayList} and marshals that instead.
         */
        @Override @SuppressWarnings("unchecked")
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

            // move data to an ArrayList
            ArrayList altered = new ArrayList((List)source);
            // and marshal that
            super.marshal(altered, writer, context);
        }

        /**
         * NOTE: using this class to unmarshal a ListProxyDefaultImpl inside Rice is unexpected, since those should
         * have been converted to ArrayLists during marshalling. However, in the interest of attempting to
         * provide a full Converter implementation here, we'll attempt to unmarshal it to an ArrayList by throwing
         * away all the funny fields that a ListProxyDefaultImpl contains (Helpfully, their names all start with "_")'
         */
        @Override @SuppressWarnings("unchecked")
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            ArrayList result = null;

            while (reader.hasMoreChildren()) {
                try {
                    reader.moveDown();

                    if (reader.getNodeName().startsWith("_")) {
                        // do nothing
                    } else {
                        if (result == null) { result = new ArrayList(); } // lazy init
                        addCurrentElementToCollection(reader, context, result, result);
                    }
                } finally {
                    reader.moveUp();
                }
            }

            return result;
        }
    }


    /**
     * Custom {@link com.thoughtworks.xstream.converters.Converter} that moves elements from a
     * {@link SetProxyDefaultImpl} into a {@link HashSet} and marshals that instead.
     */
    private static class SetProxyDefaultImplConverter extends CollectionConverter {

        SetProxyDefaultImplConverter(Mapper mapper) {
            super(mapper);
        }

        @Override @SuppressWarnings("unchecked")
        public boolean canConvert(Class type) {
            return SetProxyDefaultImpl.class.equals(type);
        }

        /**
         * moves elements from a {@link SetProxyDefaultImpl} into a {@link HashSet} and marshals that instead.
         */
        @Override @SuppressWarnings("unchecked")
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

            // move data to a HashSet
            HashSet altered = new HashSet((Set)source);
            // and marshal that
            super.marshal(altered, writer, context);
        }

        /**
         * NOTE: using this class to unmarshal a SetProxyDefaultImpl inside Rice is unexpected, since those should
         * have been converted to HashSets during marshalling. However, in the interest of attempting to
         * provide a full Converter implementation here, we'll attempt to unmarshal it to an HashSet by throwing
         * away all the funny fields that a SetProxyDefaultImpl contains (Helpfully, their names all start with "_")'
         */
        @Override @SuppressWarnings("unchecked")
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            HashSet result = null;

            while (reader.hasMoreChildren()) {
                try {
                    reader.moveDown();

                    if (reader.getNodeName().startsWith("_")) {
                        // do nothing
                    } else {
                        if (result == null) { result = new HashSet(); } // lazy init
                        addCurrentElementToCollection(reader, context, result, result);
                    }
                } finally {
                    reader.moveUp();
                }
            }

            return result;
        }
    }

}

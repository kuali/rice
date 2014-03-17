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
package org.kuali.rice.krad.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.XmlObjectSerializerService;
import org.kuali.rice.krad.service.util.DateTimeConverter;
import org.springframework.beans.factory.annotation.Required;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlObjectSerializerServiceImpl implements XmlObjectSerializerService {
	private static final Log LOG = LogFactory.getLog(XmlObjectSerializerServiceImpl.class);

	protected LegacyDataAdapter lda;

	protected XStream xstream;

	public XmlObjectSerializerServiceImpl() {
		xstream = new XStream(new ProxyAwareJavaReflectionProvider());

        // See http://xstream.codehaus.org/faq.html#Serialization_CGLIB
        // To use a newer version of XStream we may need to do something like this:
//        xstream = new XStream() {
//
//            @Override
//            public ReflectionProvider getReflectionProvider() {
//                return new ProxyAwareJavaReflectionProvider();
//            }
//
//            protected MapperWrapper wrapMapper(MapperWrapper next) {
//                return new CGLIBMapper(next);
//            }
//        };
//        xstream.registerConverter(new CGLIBEnhancedConverter(xstream.getMapper(), xstream.getReflectionProvider()));

		xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
        try {
        	Class<?> objListProxyClass = Class.forName("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
            xstream.addDefaultImplementation(ArrayList.class, objListProxyClass);
        } catch ( Exception ex ) {
        	// Do nothing - this will blow if the OJB class does not exist, which it won't in some installs
        }
        xstream.registerConverter(new DateTimeConverter());
	}

    @Required
    public void setLegacyDataAdapter(LegacyDataAdapter lda) {
        this.lda = lda;
    }

    @Override
	public String toXml(Object object) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "toXml(" + object + ") : \n" + xstream.toXML(object) );
    	}
        return xstream.toXML(object);
    }

    @Override
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
            super.marshal(lda.resolveProxy(obj), writer, context);
        }

        // we shouldn't need an unmarshal method because all proxy metadata is taken out of the XML, so we'll reserialize as a base BO.
    }

    public class ProxyAwareJavaReflectionProvider extends PureJavaReflectionProvider {

        /**
         * @see com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider#visitSerializableFields(java.lang.Object, com.thoughtworks.xstream.converters.reflection.ReflectionProvider.Visitor)
         */
        @Override
        public void visitSerializableFields(Object object, Visitor visitor) {
            for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }
                validateFieldAccess(field);
                if (ignoreField(field)) {
                    continue;
                }
                Object value = null;
                try {
                    value = field.get(object);
                    if (value != null && lda.isProxied(value)) {
                        value = lda.resolveProxy(value);
                    }
                } catch (Exception e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName() + " on " + object, e);
                }
                visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
            }
        }

        protected boolean ignoreField(Field field) {
            return false;
        }

    }

}

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

package org.kuali.core.service.impl;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Iterator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.XmlObjectSerializerService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

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
 * This class is the service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework.
 * 
 * 
 */
@Transactional
public class XmlObjectSerializerServiceImpl implements XmlObjectSerializerService {
	private static final Log LOG = LogFactory.getLog(XmlObjectSerializerServiceImpl.class);
	
	private PersistenceService persistenceService;
	
	private XStream xstream;
	
	public XmlObjectSerializerServiceImpl() {
		xstream = new XStream(new ProxyAwareJavaReflectionProvider());
		xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
	}
	
    /**
     * @see org.kuali.core.service.XmlObjectSerializer#toXml(java.lang.Object)
     */
    public String toXml(Object object) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "toXml(" + object + ") : \n" + xstream.toXML(object) );
    	}
        return xstream.toXML(object);
    }

    /**
     * @see org.kuali.core.service.XmlObjectSerializer#fromXml(java.lang.String)
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

    public String writeNode(org.w3c.dom.Node node, boolean indent) throws TransformerException {
        Source source = new DOMSource(node);
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        if (indent) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        transformer.transform(source, result);
        return writer.toString();
    }


    public class ProxyConverter extends ReflectionConverter {
        public ProxyConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
        public boolean canConvert(Class clazz) {
            return clazz.getName().indexOf("CGLIB") > -1;// || type.getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
        }

        public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
            super.marshal(getPersistenceService().resolveProxy(obj), writer, context);
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }
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
            for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }
                validateFieldAccess(field);
                Object value = null;
                try {
                    value = field.get(object);
                    if (value != null && ProxyHelper.isProxy(value)) {
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
			persistenceService = KNSServiceLocator.getPersistenceService();
		}
		return persistenceService;
	}
    
}
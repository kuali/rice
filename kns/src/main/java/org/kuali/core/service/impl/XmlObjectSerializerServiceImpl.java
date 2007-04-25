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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.core.IdentityFactoryImpl;
import org.apache.ojb.broker.core.proxy.IndirectionHandlerCGLIBImpl;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.kuali.core.service.XmlObjectSerializerService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * This class is the service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework.
 * 
 * 
 */
@Transactional
public class XmlObjectSerializerServiceImpl implements XmlObjectSerializerService {
    /**
     * @see org.kuali.core.service.XmlObjectSerializer#toXml(java.lang.Object)
     */
    public String toXml(Object object) {
        XStream xstream = new XStream();
        xstream.registerConverter(new OjbProxyConverter());
        return xstream.toXML(object);
    }

    /**
     * @see org.kuali.core.service.XmlObjectSerializer#fromXml(java.lang.String)
     */
    public Object fromXml(String xml) {
        XStream xstream = new XStream();
        xstream.registerConverter(new OjbProxyConverter());
        return xstream.fromXML(xml);
    }

    public String toXmlForMaintainables(Object object) {
        XStream xstream = new XStream();
        return xstream.toXML(object);
    }

    public Object fromXmlForMaintainables(String xml) {
        XStream xstream = new XStream();
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

    public static class OjbProxyConverter implements Converter {

        public boolean canConvert(Class type) {
            return type.getName().indexOf("CGLIB") > -1 || type.getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
        }

        public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
            Object o = null;
            if (context.get("depth") == null) {
                context.put("depth", new Integer(0));
            }
            Integer depth = (Integer) context.get("depth");
            if (depth.intValue() > 1) {
                return;
            }
            if (object instanceof ListProxyDefaultImpl) {
                List transposedList = new ArrayList();
                List proxiedList = (List) object;
                for (Iterator iter = proxiedList.iterator(); iter.hasNext();) {
                    transposedList.add(iter.next());
                }
                o = transposedList;
            }
            else {
                o = KNSServiceLocator.getPersistenceService().resolveProxy(object);
            }
            if (o == null) {
                return;
            }
            context.put("depth", new Integer(depth.intValue() + 1));
            context.convertAnother(o);
            context.put("depth", depth);
        }

        public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
            return null;
        }
    }

}
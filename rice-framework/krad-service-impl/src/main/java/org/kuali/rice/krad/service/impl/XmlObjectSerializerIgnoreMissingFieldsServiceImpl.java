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

import java.util.ArrayList;

import org.kuali.rice.krad.service.util.DateTimeConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlObjectSerializerIgnoreMissingFieldsServiceImpl extends XmlObjectSerializerServiceImpl {

	public XmlObjectSerializerIgnoreMissingFieldsServiceImpl() {

        xstream = new XStream(new ProxyAwareJavaReflectionProvider()) {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn,
                            String fieldName) {
                        if (definedIn == Object.class) {
                            return false;
                        }
                      return super.shouldSerializeMember(definedIn, fieldName);
                   }
               };
           }
       };

		xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
        try {
        	Class<?> objListProxyClass = Class.forName("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
            xstream.addDefaultImplementation(ArrayList.class, objListProxyClass);
        } catch ( Exception ex ) {
        	// Do nothing - this will blow if the OJB class does not exist, which it won't in some installs
        }
        xstream.registerConverter(new DateTimeConverter());
	}

}

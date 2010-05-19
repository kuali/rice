/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.xsd.Config;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Parses groups from XML using JAXB.
 *
 * @see KimGroups
 *
 * @author Kuali Rice Team (rice.collab@kuali.org) *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupXmlJAXBParser implements XmlConstants {
    private static final Logger LOG = Logger.getLogger(GroupXmlJAXBParser.class);

	public GroupInfo parse(InputStream in) throws IOException {
        GroupInfo groupInfo = new GroupInfo();
		JAXBContext jaxbContext;
		Unmarshaller unmarshaller;

		try {
			jaxbContext = JAXBContext.newInstance(GroupInfo.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (Exception ex) {
			throw new ConfigurationException("Error initializing JAXB for config", ex);
		}

		if (in == null) {
			LOG.warn("###############################");
			LOG.warn("#");
			LOG.warn("# XML Import input stream not found!");
			LOG.warn("#");
			LOG.warn("###############################");
		} else {
			try {
//				groupInfo = (GroupInfo) unmarshaller.unmarshal(in);  // test w/o filter
				groupInfo = unmarshal(unmarshaller, in);
			} catch (Exception ex) {
				LOG.error(ex.getMessage());
				throw new ConfigurationException("Error parsing XML input stream", ex);
			}

		}
		return groupInfo;
	}

    protected GroupInfo unmarshal(Unmarshaller unmarshaller, InputStream in) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        XMLFilter filter = new GroupNamespaceURIFilter();
        filter.setParent(spf.newSAXParser().getXMLReader());

        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
        filter.setContentHandler(handler);

        filter.parse(new InputSource(in));

        return (GroupInfo)handler.getResult();
    }

    public class GroupNamespaceURIFilter extends XMLFilterImpl {

        public static final String GROUP_URI="http://rice.kuali.org/xsd/kim/group";
        
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = GROUP_URI;
            }
            
            super.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = GROUP_URI;
            }
            
            super.endElement(uri, localName, qName);
        }
    }

}

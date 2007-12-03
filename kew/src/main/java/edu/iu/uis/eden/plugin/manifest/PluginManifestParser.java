/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.plugin.manifest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.kuali.rice.config.Config;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.plugin.PluginException;
import edu.iu.uis.eden.util.Utilities;

/**
 * Parses a {@link PluginManifest} configuration from an XML file.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginManifestParser {

    private static final String PARAMETER_TAG = "parameter";
    private static final String LISTENER_TAG = "listener";
    private static final String LISTENER_CLASS_TAG = "listener-class";
    private static final String RESOURCE_LOADER_TAG = "resourceLoader";

    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String CLASS_ATTRIBUTE = "class";

    public PluginManifest parse(File manifestFile, Config parentConfig) throws IOException, InvalidXmlException {
    	return parse(manifestFile.toURL(), parentConfig);
    }

	public PluginManifest parse(URL url, Config parentConfig) throws IOException, InvalidXmlException {
		SAXBuilder builder = new SAXBuilder(false);
		try {
            // NOTE: need to be wary of whether streams are closed properly
            // by builder
			Document doc = builder.build(url);
			Element root = doc.getRootElement();
			PluginManifest pluginManifest = new PluginManifest(url, parentConfig);
			parseResourceLoader(root, pluginManifest);
			parseListeners(root, pluginManifest);
			return pluginManifest;
		} catch (JDOMException e) {
		    throw new PluginException("Error when parsing the plugin manifest file.", e);
		}
	}

	public void parseResourceLoader(Element element, PluginManifest pluginManifest) throws InvalidXmlException {
		List loaderElements = element.getChildren(RESOURCE_LOADER_TAG);
		if (loaderElements.size() > 1) {
			throw new InvalidXmlException("Only one <resourceLoader> tag may be defined.");
		} else if (!loaderElements.isEmpty()) {
			Element loaderElement = (Element)loaderElements.get(0);
			String attributeClass = loaderElement.getAttributeValue(CLASS_ATTRIBUTE);
			if (StringUtils.isEmpty(attributeClass)) {
				throw new InvalidXmlException("<resourceLoader> element must define a 'class' attribute.");
			}
			pluginManifest.setResourceLoaderClassname(attributeClass);
		}
	}

	public void parseListeners(Element element, PluginManifest pluginManifest) throws InvalidXmlException {
		List listeners = element.getChildren(LISTENER_TAG);
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
		    pluginManifest.addListener(parseListenerProperties((Element)iterator.next()));
		}
	}

	private String parseListenerProperties(Element element) throws InvalidXmlException {
		String listenerClass = element.getChildText(LISTENER_CLASS_TAG);
		if (Utilities.isEmpty(listenerClass)) {
			throw new InvalidXmlException("Listener Class tag must have a class property defined");
		}
		return listenerClass;
	}

	public Map parseParameters(Element element) throws InvalidXmlException {
        Map parsedParms = new HashMap();
	    List parameters = element.getChildren(PARAMETER_TAG);
		for (Iterator iterator = parameters.iterator(); iterator.hasNext();) {
		    String [] parm = parseParameter((Element)iterator.next());
		    parsedParms.put(parm[0], parm[1]);
		}
		return parsedParms;
	}

	private String [] parseParameter(Element element) throws InvalidXmlException {
		String name = element.getAttributeValue(NAME_ATTRIBUTE);
		String value = element.getAttributeValue(VALUE_ATTRIBUTE);
		if (Utilities.isEmpty(name)) {
			throw new InvalidXmlException("Parameter tag must have a name attribute defined");
		}
		if (Utilities.isEmpty(value)) {
			throw new InvalidXmlException("Parameter tag must have a value attribute defined");
		}
		return new String [] {name, value};
	}


}

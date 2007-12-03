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
package edu.iu.uis.eden.edl;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * Imported into client style sheets to import other style sheets in our database into their stylesheet.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class WidgetURIResolver implements URIResolver {

	private static final Logger LOG = Logger.getLogger(WidgetURIResolver.class);

	public Source resolve(String href, String base) {

		try {
			EDocLiteService eDocSvc = KEWServiceLocator.getEDocLiteService();
			EDocLiteStyle widgetStyle = eDocSvc.getEDocLiteStyle(href);
			return new StreamSource(new StringReader(widgetStyle.getXmlContent()));

		} catch (Exception e) {
			LOG.error("Error ocurred getting style " + href, e);
		}
		return null;
	}

}
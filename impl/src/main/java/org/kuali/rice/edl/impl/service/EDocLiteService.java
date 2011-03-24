/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.edl.impl.service;

import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import org.kuali.rice.edl.impl.EDLController;
import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.kew.xml.XmlLoader;
import org.kuali.rice.kew.xml.export.XmlExporter;
import org.w3c.dom.Document;


public interface EDocLiteService extends XmlLoader, XmlExporter {
	//looks like these are here only for tests, the question is why...
    public void saveEDocLiteStyle(InputStream xml);
    public void saveEDocLiteDefinition(InputStream xml);
    public void saveEDocLiteAssociation(InputStream xml);

    public EDocLiteStyle getEDocLiteStyle(String styleName);
    public EDocLiteDefinition getEDocLiteDefinition(String defName);
    public EDocLiteAssociation getEDocLiteAssociation(String docType);
    public EDocLiteAssociation getEDocLiteAssociation(Long associationId);

    public List<String> getEDocLiteStyles();
    public List<EDocLiteDefinition> getEDocLiteDefinitions();
    public List<EDocLiteAssociation> getEDocLiteAssociations();

    public void removeStyleFromCache(String styleName);
    public void removeDefinitionFromCache(String edlName);
    public Templates getStyleAsTranslet(String styleName) throws TransformerConfigurationException;
    public List<EDocLiteAssociation> search(EDocLiteAssociation edocLite);

    public EDLController getEDLController(String edlName);
	public EDLController getEDLController(Long documentTypeId);
	public void initEDLGlobalConfig();
	public void saveEDocLiteStyle(EDocLiteStyle data);
    public void saveEDocLiteDefinition(EDocLiteDefinition data) ;
    public void saveEDocLiteAssociation(EDocLiteAssociation assoc);
    public Document getDefinitionXml(EDocLiteAssociation edlAssociation);
}

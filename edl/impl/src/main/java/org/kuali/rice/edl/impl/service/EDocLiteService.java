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

import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.framework.impex.xml.XmlLoader;
import org.kuali.rice.edl.impl.EDLController;
import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;
import org.w3c.dom.Document;


public interface EDocLiteService extends XmlLoader, XmlExporter {

	public void saveEDocLiteDefinition(InputStream xml);
    public void saveEDocLiteAssociation(InputStream xml);

    public EDocLiteDefinition getEDocLiteDefinition(String defName);
    public EDocLiteAssociation getEDocLiteAssociation(String docType);
    public EDocLiteAssociation getEDocLiteAssociation(Long associationId);

    public List<EDocLiteDefinition> getEDocLiteDefinitions();
    public List<EDocLiteAssociation> getEDocLiteAssociations();

    public void removeDefinitionFromCache(String edlName);
    public Templates getStyleAsTranslet(String styleName) throws TransformerConfigurationException;
    public List<EDocLiteAssociation> search(EDocLiteAssociation edocLite);

    public EDLController getEDLControllerUsingEdlName(String edlName);
	public EDLController getEDLControllerUsingDocumentId(String documentId);
	public void initEDLGlobalConfig();
    public void saveEDocLiteDefinition(EDocLiteDefinition data) ;
    public void saveEDocLiteAssociation(EDocLiteAssociation assoc);
    public Document getDefinitionXml(EDocLiteAssociation edlAssociation);
}

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

import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.xml.export.XmlExporter;

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
    public List getEDocLiteDefinitions();
    public List getEDocLiteAssociations();
    
    public void removeStyleFromCache(String styleName);
    public void removeDefinitionFromCache(String edlName);
    public Templates getStyleAsTranslet(String styleName) throws TransformerConfigurationException;
    public List search(EDocLiteAssociation edocLite);
    
    public EDLController getEDLController(String edlName);
	public EDLController getEDLController(Long documentTypeId);
	public void initEDLGlobalConfig();
	public void saveEDocLiteStyle(EDocLiteStyle data);
    public void saveEDocLiteDefinition(EDocLiteDefinition data) ;
    public void saveEDocLiteAssociation(EDocLiteAssociation assoc);
    public Document getDefinitionXml(EDocLiteAssociation edlAssociation); 
}
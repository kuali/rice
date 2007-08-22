/*
 * Copyright 2005-2007 The Kuali Foundation.
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
// Created on Jan 4, 2007

package edu.iu.uis.eden.edl;

import java.io.InputStream;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.xml.export.XmlExporter;

public interface StyleService extends XmlLoader, XmlExporter {
    public void saveStyle(InputStream xml);
    public EDocLiteStyle getStyle(String styleName);
    public List<String> getStyleNames();
    public List<EDocLiteStyle> getStyles();
    public void removeStyleFromCache(String styleName);
    public Templates getStyleAsTranslet(String styleName) throws TransformerConfigurationException;
    public void saveStyle(EDocLiteStyle data);
}
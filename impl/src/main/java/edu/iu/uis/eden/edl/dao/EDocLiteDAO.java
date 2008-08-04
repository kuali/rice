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
package edu.iu.uis.eden.edl.dao;

import java.util.List;

import edu.iu.uis.eden.edl.EDocLiteAssociation;
import edu.iu.uis.eden.edl.EDocLiteDefinition;
import edu.iu.uis.eden.edl.EDocLiteStyle;

public interface EDocLiteDAO {
    public void saveEDocLiteStyle(EDocLiteStyle style);
    public void saveEDocLiteDefinition(EDocLiteDefinition definition);
    public void saveEDocLiteAssociation(EDocLiteAssociation assoc);

    public EDocLiteStyle getEDocLiteStyle(String styleName);
    public EDocLiteDefinition getEDocLiteDefinition(String defName);
    public EDocLiteAssociation getEDocLiteAssociation(String documentTypeName);
    public EDocLiteAssociation getEDocLiteAssociation(Long associationId);

    public List<String> getEDocLiteStyleNames();
    public List<EDocLiteStyle> getEDocLiteStyles();

    public List getEDocLiteDefinitions();
    public List getEDocLiteAssociations();
    
    public List search(EDocLiteAssociation edocLite);
}
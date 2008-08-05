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
package org.kuali.rice.kew.docsearch.dao;

import java.util.List;

import org.kuali.rice.kew.docsearch.DocSearchCriteriaVO;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;

import edu.iu.uis.eden.user.WorkflowUser;

public interface DocumentSearchDAO {
    public List getListBoundedByCritera(DocumentSearchGenerator documentSearchGenerator, DocSearchCriteriaVO criteria, WorkflowUser user) throws EdenUserNotFoundException;
    public List getList(DocumentSearchGenerator docSearchGenerator,DocSearchCriteriaVO criteria, WorkflowUser user) throws EdenUserNotFoundException;
}
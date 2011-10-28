/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krms.impl.ui;


import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.impl.repository.ContextValidTermBo;
import org.kuali.rice.krms.impl.repository.TermBo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ValidTermsValuesFinder extends KeyValuesBase {

    @Override
	public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        //TODO: get the list of valid terms for this context. (currently gets all)
        Collection<ContextValidTermBo> contextValidTerms = KRADServiceLocator.getBusinessObjectService().findAll(ContextValidTermBo.class);
        List<String> termSpecIds = new ArrayList();
        for (ContextValidTermBo validTerm : contextValidTerms) {
            termSpecIds.add(validTerm.getTermSpecificationId());
        }
        Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put("term_spec_id", termSpecIds);
        Collection<TermBo> terms = KRADServiceLocator.getBusinessObjectService().findMatching(TermBo.class, criteria);

        for (TermBo term : terms) {
            keyValues.add(new ConcreteKeyValue(term.getId(), term.getDescription()));
        }
        return keyValues;
    }
}
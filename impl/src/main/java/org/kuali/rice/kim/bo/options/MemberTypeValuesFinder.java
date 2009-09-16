/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MemberTypeValuesFinder extends KeyValuesBase {

	static final List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>( 3 );
	static {
        labels.add(new KeyLabelPair(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL));
        labels.add(new KeyLabelPair(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_GROUP));
        labels.add(new KeyLabelPair(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE));
	}
	
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        return labels;
    }    

}

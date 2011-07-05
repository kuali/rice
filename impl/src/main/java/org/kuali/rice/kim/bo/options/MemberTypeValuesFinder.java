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
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MemberTypeValuesFinder extends KeyValuesBase {

	private static final List<KeyValue> LABELS;
	static {
		final List<KeyValue> labels = new ArrayList<KeyValue>( 3 );
        labels.add(new ConcreteKeyValue(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL));
        labels.add(new ConcreteKeyValue(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_GROUP));
        labels.add(new ConcreteKeyValue(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE));
        LABELS = Collections.unmodifiableList(labels);
	}
	
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
	public List<KeyValue> getKeyValues() {
        return LABELS;
    }    

}

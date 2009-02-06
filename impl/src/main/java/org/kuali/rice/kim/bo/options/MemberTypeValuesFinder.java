/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MemberTypeValuesFinder extends KeyValuesBase {

	public static final String MEMBER_TYPE_PRINCIPAL_CODE = "P";
	public static final String MEMBER_TYPE_GROUP_CODE = "G";
	public static final String MEMBER_TYPE_ROLE_CODE = "R";
	public static final String MEMBER_TYPE_PRINCIPAL = "Principal";
	public static final String MEMBER_TYPE_GROUP = "Group";
	public static final String MEMBER_TYPE_ROLE = "Role";
	
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair(MEMBER_TYPE_PRINCIPAL_CODE, MEMBER_TYPE_PRINCIPAL));
        labels.add(new KeyLabelPair(MEMBER_TYPE_GROUP_CODE, MEMBER_TYPE_GROUP));
        labels.add(new KeyLabelPair(MEMBER_TYPE_ROLE_CODE, MEMBER_TYPE_ROLE));
        return labels;
    }    

}

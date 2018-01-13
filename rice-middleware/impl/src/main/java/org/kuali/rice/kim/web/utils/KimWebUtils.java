/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.web.utils;

import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimWebUtils {

    public static boolean matchingQualifierExists(List<KimDocumentRoleMember> members,Integer index,String kimAttrDefnId){
        if(null != members)  {
            KimDocumentRoleMember member = members.get(index);
            if(null != member && null == member.getQualifier(kimAttrDefnId)){
                return false;
            } else {
                 return true;
            }
        } else {
            return false;
        }
    }
}

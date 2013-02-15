/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.krms.framework.engine.expression.ComparisonOperator;

/**
 * Created with IntelliJ IDEA.
 * User: SW
 * Date: 2012/08/02
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class KrmsStudentMockServiceImpl {

    public String getTermForType(String type){
        if( "20000".equals(type) || "20007".equals(type) || "20009".equals(type)){
            return "20000";
        } else if ( "20001".equals(type)){
            return "20001";
        } else if ( "20002".equals(type) || "20003".equals(type) || "20008".equals(type)){
            return "20002";
        }
        return "";
    }

    public String getOperationForType(String type){
        if( "20000".equals(type)){
            return ComparisonOperator.GREATER_THAN_EQUAL.getCode();
        }
        return ComparisonOperator.EQUALS.getCode();
    }

    public String getValueForType(String type){
        if( "20000".equals(type)){
            return "?";
        } else if ( "20001".equals(type)){
            return "true";
        } else if ( "20002".equals(type)){
            return "true";
        } else if ( "20003".equals(type)){
            return "false";
        }
        return "";
    }
}

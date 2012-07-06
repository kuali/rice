/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif;

import java.awt.print.Pageable;

/**
 * Created by IntelliJ IDEA.
 * User: sonam
 * Date: 7/3/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AjaxReturnTypes {
     UPADATEPAGE("update-page"), UPDATEPAGEERRORS("update-pageErrors"), UPDATECOMPONENT("update-component"), REDIRECT("redirect"), SHOWINCIDENT("show-incident")  ;

     private String key;

     AjaxReturnTypes(String key) {
         this.key = key;
     }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

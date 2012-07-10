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
package org.kuali.rice.krad.bo;

import org.kuali.rice.core.api.mo.ModelObjectBasic;

/**
 * Interface for business objects
 *
 * <p>
 * Business objects are special objects to the Rice framework that indicate an object has certain features
 * (like refresh), most business objects are persistable see {@link PersistableBusinessObjectBase}
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface BusinessObject extends ModelObjectBasic {
    
    /**
     * Refreshes any reference objects from the primitive values.
     */
    public abstract void refresh();
}

/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kew.bo;

import java.io.Serializable;

public interface WorkflowPersistable extends Serializable {

    /**
     * @deprecated this method is dangerous and not really deterministic, especially in regards to
     * circular references, etc.  In most of the cases where we use this, we are using it to simply
     * strip primary keys and lock version numbers from object graphs.
     */
  public Object copy(boolean preserveKeys);
  
}

/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.api.attribute;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;

/**
 * Created by IntelliJ IDEA.
 * User: jjhanso
 * Date: 4/5/11
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */
public interface KimAttributeContract extends Versioned, GloballyUnique {
    String getId();
    String getKimTypeId();
    String getAttributeId();
    String getValue();
}

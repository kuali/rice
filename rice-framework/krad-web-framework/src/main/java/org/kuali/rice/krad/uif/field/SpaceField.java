/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;

/**
 * Field that produces only a space
 *
 * <p>
 * Can be used to aid in the layout of other fields, for instance in a grid. For
 * example in a totals row generally the rows that are not totaled are blank in
 * the total row.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "spaceField", parent = "Uif-SpaceField")
public class SpaceField extends FieldBase {
    private static final long serialVersionUID = -4740343801872334348L;

    public SpaceField() {
        super();
    }

}

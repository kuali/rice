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
package org.kuali.rice.krad.datadictionary.validation.constraint.resolver;

import org.kuali.rice.krad.datadictionary.validation.capability.CaseConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;

import java.util.Collections;
import java.util.List;

/**
 * CaseConstraintResolver returns the case constraint as a list for a definition implementing the capability {@link
 * CaseConstrainable}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @param <T> constrainable type
 */
public class CaseConstraintResolver<T extends CaseConstrainable> implements ConstraintResolver<T> {

    @Override
    public <C extends Constraint> List<C> resolve(T definition) {
        @SuppressWarnings("unchecked") C caseConstraint = (C) definition.getCaseConstraint();
        return (caseConstraint == null) ? null : Collections.<C>singletonList(caseConstraint);
    }

}

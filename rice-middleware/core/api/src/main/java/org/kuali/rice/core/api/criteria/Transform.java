/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

/**
 * Works with the lookup framework to customize a query or result.

 * <p>
 * The transform will applied to the input yielding either a same or different typed output.  Normally, these are either
 * {@link Predicate}s or {@link QueryByCriteria}.  If the input does not need to be transformed then the function can
 * return the incoming argument.  This is a way to add, change, or remove inputs before or after query execution.  If
 * the input should be removed, the transform should return null.  The classic use of {@code Transform}s are to allow a
 * predicate referencing a property path that does not exist on a database mapped object to be changed to something that
 * is valid.
 * </p>
 *
 * <p>
 * The result transform will be applied to the results of the query after the query is executed.
 * If the result does not need to be transformed then the function can return the
 * incoming argument. This is a way to remove or change a result after the query is executed.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Transform<P, R> {

    /**
     * Applies the transformation to the {@code input}.
     *
     * @param input the entity before the transformation
     *
     * @return a transformed {@code input} or null if it should be removed
     */
    R apply(P input);

}
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
package org.kuali.rice.krad.uif.layout;

import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.field.FieldGroup;

/**
 * Layout manager implementations that work with a collection (such as a table layout) should implement
 * this interface for building the collection component instances.
 *
 * <p>Unlike other group instances, collection group instances need to generate new instances of the
 * configured components for each line of the collection. The field instances for each line
 * are wrapped differently depending on what layout manager is being applied. Therefore as the collection lines
 * are being built (during the applyModel phase) this method will be invoked on the manager so that it may
 * setup the line as needed.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.container.CollectionGroupBuilder
 */
public interface CollectionLayoutManager extends LayoutManager {

    /**
     * Call to the layout manager to build the components necessary for the given collection line,
     * within an active view lifecycle.
     *
     * <p>As the collection is being iterated over by the {@link org.kuali.rice.krad.uif.container.CollectionGroupLineBuilder}
     * this method is invoked for each line. The builder will create copies of the configured fields and actions for
     * the line and pass into the layout manager so they can be assembled</p>
     *
     * @param lineBuilderContext context for the line to be built
     */
    void buildLine(LineBuilderContext lineBuilderContext);

    /**
     * Field group instance that is used as a prototype for creating the sub-collection field groups.
     *
     * @return GroupField instance to use as prototype
     */
    FieldGroup getSubCollectionFieldGroupPrototype();

    /**
     * Invoked when a paging request occurs to carry out the paging request.
     *
     * @param model object containing the view's data
     * @param collectionGroup collection group the request was made for
     */
    void processPagingRequest(Object model, CollectionGroup collectionGroup);
}

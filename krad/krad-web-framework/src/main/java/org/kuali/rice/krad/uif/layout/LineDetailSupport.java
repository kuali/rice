/*
 * Copyright 2006-2012 The Kuali Foundation
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
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.view.View;

/**
 * Inteferface specifying methods necessary to define line details (aka master detail) for a collection.
 * LayoutManagers
 * using this interface allow details to be disclosed in for a collection line in the UI
 */
public interface LineDetailSupport {
    /**
     * Creates the details group for the line using the information setup through the setter methods of this
     * interface.  Line details are currently only supported in TableLayoutManagers which use richTable.
     *
     * @param collectionGroup
     * @param view
     */
    public void setupDetails(CollectionGroup collectionGroup, View view);

    /**
     * The row details info group to use when using a TableLayoutManager with the a richTable.  This group will be
     * displayed when the user clicks the "Details" link/image on a row.  This allows extra/long data to be
     * hidden in table rows and then revealed during interaction with the table without the need to
     * leave the page.  Allows for any group content.
     *
     * Does not currently work with javascript required content.
     *
     * @return rowDetailsGroup component
     */
    public Group getRowDetailsGroup();

    /**
     * Set the row details info group
     *
     * @param rowDetailsGroup
     */
    public void setRowDetailsGroup(Group rowDetailsGroup);

    /**
     * Name of the link for displaying row details in a TableLayoutManager CollectionGroup
     *
     * @return name of the link
     */
    public String getRowDetailsLinkName();

    /**
     * Row details link name
     *
     * @param rowDetailsLinkName
     */
    public void setRowDetailsLinkName(String rowDetailsLinkName);

    /**
     * If true, the row details link will use an image instead of a link to display row details in
     * a TableLayoutManager CollectionGroup
     *
     * @return true if displaying an image instead of a link for row details
     */
    public boolean isRowDetailsUseImage();

    /**
     * Sets row details link use image flag
     *
     * @param rowDetailsUseImage
     */
    public void setRowDetailsUseImage(boolean rowDetailsUseImage);
}

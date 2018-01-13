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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * The NextPager widget is used to display previous and next links.  This widget needs to
 * know the numberOfPages total, and the currentPage the user is on currently, so this widget must be fed this
 * information from the code.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.layout.StackedLayoutManager
 */
@BeanTag(name = "nextPager", parent = "Uif-NextPager")
public class NextPager extends Pager {
    private static final long serialVersionUID = 181885730680331424L;

    private boolean centeredLinks;

    /**
     * When false, links will receive the next and previous classes which left and right align the links instead
     * of centering them.
     */
    @BeanTagAttribute
    public boolean isCenteredLinks() {
        return centeredLinks;
    }

    /**
     * @see NextPager#isCenteredLinks()
     */
    public void setCenteredLinks(boolean centeredLinks) {
        this.centeredLinks = centeredLinks;
    }
}

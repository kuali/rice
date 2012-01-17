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
package org.kuali.rice.krad.uif.container;

/**
 * Special <code>Group</code> that presents a grouping on links, which can
 * also include nested groupings of links
 *
 * <p>
 * Generally this group outputs a list of <code>LinkField</code> instances, however
 * it can be configured to place separates between the fields and also delimiters
 * for the grouping
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkGroup extends Group {
    private static final long serialVersionUID = -4173031543626881250L;

    private String groupBeginDelimiter;
    private String groupEndDelimiter;
    private String linkSeparator;
    private String emptyLinkGroupString;

    public LinkGroup() {
        super();
    }

    /**
     * String that will be rendered before the group of links are rendered
     *
     * <p>
     * If the list of links is empty, the start delimiter will not be
     * rendered but instead the #emptyLinkGroupString will be outputted
     * </p>
     *
     * e.g. '['
     *
     * @return String group begin delimiter
     */
    public String getGroupBeginDelimiter() {
        return groupBeginDelimiter;
    }

    /**
     * Setter for the group begin delimiter
     *
     * @param groupBeginDelimiter
     */
    public void setGroupBeginDelimiter(String groupBeginDelimiter) {
        this.groupBeginDelimiter = groupBeginDelimiter;
    }

    /**
     * String that will be rendered after the group of links are rendered
     *
     * <p>
     * If the list of links is empty, the end delimiter will not be
     * rendered but instead the #emptyLinkGroupString will be outputted
     * </p>
     *
     * e.g. ']'
     *
     * @return String group end delimiter
     */
    public String getGroupEndDelimiter() {
        return groupEndDelimiter;
    }

    /**
     * Setter for the group end delimiter
     *
     * @param groupEndDelimiter
     */
    public void setGroupEndDelimiter(String groupEndDelimiter) {
        this.groupEndDelimiter = groupEndDelimiter;
    }

    /**
     * String that will be rendered between each rendered link
     *
     * e.g. '|'
     *
     * @return String link separator
     */
    public String getLinkSeparator() {
        return linkSeparator;
    }

    /**
     * Setter for the link separator
     *
     * @param linkSeparator
     */
    public void setLinkSeparator(String linkSeparator) {
        this.linkSeparator = linkSeparator;
    }

    /**
     * String that will be outputted when the list backing the
     * link group is empty
     *
     * @return String empty group string
     */
    public String getEmptyLinkGroupString() {
        return emptyLinkGroupString;
    }

    /**
     * Setter for the empty group string
     *
     * @param emptyLinkGroupString
     */
    public void setEmptyLinkGroupString(String emptyLinkGroupString) {
        this.emptyLinkGroupString = emptyLinkGroupString;
    }
}

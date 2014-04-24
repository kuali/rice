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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.LifecycleElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Element which shows a visual progress bar based on percentageValue passed in or segmentPercentages passed in
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "progressBar-bean", parent = "Uif-ProgressBar"),
        @BeanTag(name = "progressBarVertical-bean", parent = "Uif-ProgressBar-Vertical")})
public class ProgressBar extends ContentElementBase {
    private static final long serialVersionUID = -2643777398164666573L;

    private Integer percentComplete;

    private List<Integer> segmentPercentages;
    private List<String> segmentSizes;
    private List<String> segmentClasses;

    private boolean vertical;

    public ProgressBar() {
        segmentSizes = new ArrayList<String>();
        segmentClasses = new ArrayList<String>();
        this.setRole(UifConstants.AriaRoles.PROGRESS_BAR);
    }

    /**
     * Sets the appropriate classes and bar widths based on values in percentComplete or segmentPercentages
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        // Css property used by bars based on vertical flag (width or height)
        String cssDimension = CssConstants.WIDTH;
        if (vertical) {
            cssDimension = CssConstants.HEIGHT;
        }

        boolean explicitlySetSizes = segmentPercentages != null && !getSegmentPercentages().isEmpty();

        // Simply use the percentage if set, and no explicitly set sizes (use those instead if set)
        if (!explicitlySetSizes && percentComplete != null) {
            segmentClasses = new ArrayList<String>();

            // Add appropriate style string based on dimension and percentage
            segmentSizes.add(cssDimension + percentComplete + "%");
            segmentClasses.add(
                    CssConstants.ProgressBar.PROGRESS_BAR + " " + CssConstants.ProgressBar.SUCCESS_PROGRESS_BAR);

            this.setTitle(percentComplete.toString() + "%");

            // Set aria attributes
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MIN, "0");
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MAX, "100");
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_NOW, percentComplete.toString());
        } else if (explicitlySetSizes && !(this instanceof StepProgressBar)) {
            if (segmentClasses == null || segmentClasses.size() != segmentPercentages.size()) {
                throw new RuntimeException(
                        "If segmentPercentages are set on a base ProgressBar type, segmentClasses must "
                                + "also be explicitly set and contain the same number of items");
            }

            // Add appropriate style string based on dimension and percentage
            percentComplete = 0;
            for (int index = 0; index < segmentPercentages.size(); index++) {
                segmentSizes.add(cssDimension + segmentPercentages.get(index) + "%");
                percentComplete = percentComplete + segmentPercentages.get(index);
            }

            // Set aria attributes
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MIN, "0");
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MAX, "100");
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_NOW, percentComplete.toString());
        }

    }

    /**
     * Percent value complete used to visually indicate this ProgressBar's completeness
     *
     * @return the Integer value representing perecent complete for this ProgressBar
     */
    @BeanTagAttribute(name = "percentComplete")
    public Integer getPercentComplete() {
        return percentComplete;
    }

    /**
     * @see ProgressBar#getPercentComplete()
     */
    public void setPercentComplete(Integer percentComplete) {
        this.percentComplete = percentComplete;
    }

    /**
     * List of bar classes to use for each bar "section" for coloring or styling purposes, only settable for
     * full configuration purposes and should not normally be set
     *
     * <p>These are normally set automatically by the framework, but can be explicitly defined if segmentPercentages
     * are also explicitly set.  When setting segmentClasses, it's list size MUST equal segmentPercentages.</p>
     *
     * @return the list of bar classes
     */
    @BeanTagAttribute(name = "segmentClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getSegmentClasses() {
        return segmentClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.ProgressBar#getSegmentClasses()
     */
    public void setSegmentClasses(List<String> segmentClasses) {
        this.segmentClasses = segmentClasses;
    }

    /**
     * The percentage each bar "section" will take up on the progress bar, only settable for
     * full configuration purposes and should not normally be set
     *
     * <p>This is normally automatically set by the framework with no additional configuration.
     * When explicitly set for ProgressBars, barClases should ALSO be set
     * (this however is optional for StepProgressBars).
     * The percentages effects how much space each section will take up on the bar and the total should not
     * exceed 100.  For StepProgressBars, this list's size must equal the number of steps.</p>
     *
     * @return the bar percentages to use
     */
    @BeanTagAttribute(name = "segmentPercentages", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<Integer> getSegmentPercentages() {
        return segmentPercentages;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.ProgressBar#getSegmentPercentages()
     */
    public void setSegmentPercentages(List<String> segmentPercentages) {
        // Note: This is purposely taking in a list of String to make bean configuration easier
        if (this.segmentPercentages == null) {
            this.segmentPercentages = new ArrayList<Integer>();
        }

        for (String percentage : segmentPercentages) {
            this.segmentPercentages.add(new Integer(percentage));
        }
    }

    /**
     * The bar sizes as String css style properties (ie, "width: 20%") in a list, framework only,
     * not settable.
     *
     * @return the bar sizes as String css style properties
     */
    public List<String> getSegmentSizes() {
        return segmentSizes;
    }

    /**
     * True if this ProgressBar should render vertical (this requires a defined height - which by default is
     * defined in the css, or by the framework for StepProgressBars), false otherwise
     *
     * @return true if this
     */
    @BeanTagAttribute(name = "vertical")
    public boolean isVertical() {
        return vertical;
    }

    /**
     * @see ProgressBar#isVertical()
     */
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

}

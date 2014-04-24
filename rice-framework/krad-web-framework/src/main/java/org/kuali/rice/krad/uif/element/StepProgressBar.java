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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.LifecycleElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Element which shows a visual progress bar alongside a set of steps, to be used by wizard or multi-step
 * processes, which reflects the current progress based on value of currentStep.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "stepProgressBar-bean", parent = "Uif-StepProgressBar")
public class StepProgressBar extends ProgressBar {
    private static final long serialVersionUID = 1053164737424481519L;

    private Map<String, String> steps;
    private List<String> stepLabelClasses;
    private List<String> accessibilityText;

    private String currentStep;
    private String completeStep;

    private Integer verticalHeight;
    private Integer verticalStepHeight;

    public StepProgressBar() {
        steps = new LinkedHashMap<String, String>();
        accessibilityText = new ArrayList<String>();
        stepLabelClasses = new ArrayList<String>();
    }

    /**
     * Populates the stepLabelClasses, accessibilityText, segmentSizes, and segmentClasses lists based on the settings
     * of this StepProgressBar.
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        // If a percentageComplete value is set, use it to try to determine the current step, otherwise if currentStep
        // is set just use that (null percentComplete value)
        if (this.getPercentComplete() != null && currentStep == null) {
            calculateCurrentStepFromPercentage();
        } else if (currentStep != null) {
            this.setPercentComplete(null);
        }

        super.performFinalize(model, parent);

        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        // Initializing and checking for validity of values:
        String cssDimension = CssConstants.WIDTH;
        if (this.isVertical()) {
            cssDimension = CssConstants.HEIGHT;
            this.addStyleClass(CssConstants.ProgressBar.VERTICAL_STEP_PROGRESS_BAR);
        }

        int totalSteps = steps.size();
        if (totalSteps == 0) {
            throw new RuntimeException(
                    "At least one step is required for a StepProgressBar: " + this.getId() + " with parent: " + parent
                            .getId());
        }

        boolean explicitlySetPercentages = CollectionUtils.isNotEmpty(getSegmentPercentages());
        boolean explicitlySetClasses = CollectionUtils.isNotEmpty(this.getSegmentClasses());
        if (explicitlySetPercentages && explicitlySetClasses && this.getSegmentClasses().size() != this
                .getSegmentPercentages().size()) {
            throw new RuntimeException(
                    "If segmentPercentages are set on a StepProgressBar type, and segmentClasses are also "
                            + "set, the lists MUST contain the same number of items");
        }

        // Populate the information used by the template based on settings of this StepProgressBar
        populateProgressBarRenderingLists(totalSteps, cssDimension, explicitlySetPercentages, explicitlySetClasses);

        // Explicitly set the vertical height for vertical cases where the verticalHeight is not set using
        // verticalStepHeight
        if (this.isVertical() && getVerticalHeight() == null) {
            setVerticalHeight(getSegmentSizes().size() * verticalStepHeight);
        }

        // If the step is considered complete, set the aria attributes appropriately
        if (currentStep != null && currentStep.equals(completeStep)) {
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_NOW, Integer.toString(steps.size()));
            this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_TEXT, messageService.getMessageText(
                    "accessibility.progressBar.complete"));
        }

        // Add aria attributes
        this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MIN, "0");
        this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_MAX, Integer.toString(totalSteps));
    }

    /**
     * Calculate the current step based on a percentage value.
     *
     * @return the current step key which is at that percentage of total steps
     */
    private String calculateCurrentStepFromPercentage() {
        if (getPercentComplete() == 0) {
            return "";
        } else if (getPercentComplete() == 100) {
            return completeStep;
        }

        int size = steps.size();
        double currentStep = Math.ceil(size * this.getPercentComplete());

        String key = "";
        Iterator<String> stepIterator = steps.keySet().iterator();
        for (int step = 0; stepIterator.hasNext() && step <= currentStep; step++) {
            key = stepIterator.next();
        }

        this.setPercentComplete(null);

        return key;
    }

    /**
     * Populate the information used by the template based on settings of this StepProgressBar by iterating of
     * the steps and setting classes and other rendering info in list to be used by the template.
     *
     * @param totalSteps the total steps in this StepProgressBar
     * @param cssDimension the css dimension property to use for bar sizes
     * @param explicitlySetPercentages true if bar percentages were manually set
     * @param explicitlySetClasses true if bar classes wer manually set
     */
    public void populateProgressBarRenderingLists(int totalSteps, String cssDimension, boolean explicitlySetPercentages,
            boolean explicitlySetClasses) {
        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        double percentage = Math.floor(100 / totalSteps);
        double percentTotal = 0;
        boolean currentStepFound = false;

        // Bar is considered empty if currentStep is not set or is something that does not match a key
        // so set currentStepFound to true to force the following loop to only create "empty" bars
        if (StringUtils.isBlank(currentStep) || (!steps.containsKey(currentStep) && !currentStep.equals(
                completeStep))) {
            currentStepFound = true;
        }

        Iterator<String> stepIterator = steps.keySet().iterator();
        for (int step = 0; stepIterator.hasNext() && step <= totalSteps; step++) {
            String stepKey = stepIterator.next();

            double stepPercentage;

            // Retrieve/calculate the current stepPercentage and current percentageTotal of bars being processed
            if (explicitlySetPercentages) {
                Integer percentageValue = getSegmentPercentages().get(step);
                stepPercentage = percentageValue;
                percentTotal += percentageValue;
            } else {
                stepPercentage = percentage;
                percentTotal += percentage;
            }

            // if there is some missing width to make 100% due to uneven division and we are on the final iteration,
            // give the additional percentage to the last bar
            if (!stepIterator.hasNext() && percentTotal < 100) {
                stepPercentage = (percentage + (100 - percentTotal));
                percentTotal = 100;
            }

            String dimensionValue = stepPercentage + "%";

            // Default bar styles and screen reader text
            String cssClasses =
                    CssConstants.ProgressBar.PROGRESS_BAR + " " + CssConstants.ProgressBar.SUCCESS_PROGRESS_BAR;
            String labelCssClasses = CssConstants.ProgressBar.STEP_LABEL + " " + CssConstants.ProgressBar.COMPLETE;
            String srText = messageService.getMessageText("accessibility.progressBar.completeStep");

            // If current step, change styles and text appropriately.  When the step has already be found,
            // the final bars are considered empty/incomplete steps
            if (stepKey.equals(currentStep)) {
                currentStepFound = true;
                cssClasses = CssConstants.ProgressBar.PROGRESS_BAR + " " + CssConstants.ProgressBar.INFO_PROGRESS_BAR;
                labelCssClasses = CssConstants.ProgressBar.STEP_LABEL + " " + CssConstants.ProgressBar.ACTIVE;
                srText = messageService.getMessageText("accessibility.progressBar.currentStep");

                // Set aria attributes for the current value
                this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_NOW, Integer.toString(step));
                this.addAriaAttribute(UifConstants.AriaAttributes.VALUE_TEXT, srText + steps.get(stepKey));
            } else if (currentStepFound) {
                cssClasses = CssConstants.ProgressBar.PROGRESS_BAR + " " + CssConstants.ProgressBar.EMPTY_PROGRESS_BAR;
                labelCssClasses = CssConstants.ProgressBar.STEP_LABEL;
                srText = messageService.getMessageText("accessibility.progressBar.futureStep");
            }

            this.getSegmentSizes().add(cssDimension + dimensionValue);

            // Don't add default classes if custom classes have been set for the bars
            if (!explicitlySetClasses) {
                this.getSegmentClasses().add(cssClasses);
            }

            this.getStepLabelClasses().add(labelCssClasses);
            this.accessibilityText.add(srText);
        }
    }

    /**
     * The steps as key-value pairs for this StepProgressBar, where value is human-readable text.
     *
     * @return the map of steps for this StepProgressBar
     */
    @BeanTagAttribute(name = "steps", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getSteps() {
        return steps;
    }

    /**
     * @see StepProgressBar#getSteps()
     */
    public void setSteps(Map<String, String> steps) {
        this.steps = steps;
    }

    /**
     * The list of step values; framework only, not settable.
     *
     * @return the list of step values
     */
    public Collection<String> getStepCollection() {
        return steps.values();
    }

    /**
     * The list of step label css classes in order of steps shown; framework only, not settable
     *
     * @return the list of step label css classes
     */
    public List<String> getStepLabelClasses() {
        return stepLabelClasses;
    }

    /**
     * The list of additional screen reader only accessibility text to render per step, in order; framework only,
     * not settable.
     *
     * @return the list of additional screen reader only accessibility text
     */
    public List<String> getAccessibilityText() {
        return accessibilityText;
    }

    /**
     * The current step (by key) of this progress bar to be highlighted visually as the active step.
     *
     * @return the current step (by key)
     */
    @BeanTagAttribute(name = "currentStep")
    public String getCurrentStep() {
        return currentStep;
    }

    /**
     * @see StepProgressBar#getCurrentStep()
     */
    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * The key that when currentStep has this value, shows the step progress bar as fully complete; this key
     * is must not be part of the steps being shown, by default this has a value of "SUCCESS".
     *
     * @return the completeStep key for showing this bar as fully complete
     */
    @BeanTagAttribute(name = "completeStep")
    public String getCompleteStep() {
        return completeStep;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.StepProgressBar#getCompleteStep()
     */
    public void setCompleteStep(String completeStep) {
        this.completeStep = completeStep;
    }

    /**
     * The height (in pixels) of the progress bar portion of this component, if this is not set, verticalStepHeight
     * is used to calculate this value; only used when vertical property is true.
     *
     * @return the verticalHeight of the progress bar
     */
    @BeanTagAttribute(name = "verticalHeight")
    public Integer getVerticalHeight() {
        return verticalHeight;
    }

    /**
     * @see StepProgressBar#getVerticalHeight()
     */
    public void setVerticalHeight(Integer verticalHeight) {
        this.verticalHeight = verticalHeight;
    }

    /**
     * The height (in pixels) allocated for each step for vertical step display used to calculate verticalHeight if not
     * set, by default this is 75.
     *
     * @return the vertical step height used to calculate verticalHeight
     */
    @BeanTagAttribute(name = "verticalStepHeight")
    public Integer getVerticalStepHeight() {
        return verticalStepHeight;
    }

    /**
     * @see StepProgressBar#getVerticalStepHeight()
     */
    public void setVerticalStepHeight(Integer verticalStepHeight) {
        this.verticalStepHeight = verticalStepHeight;
    }
}

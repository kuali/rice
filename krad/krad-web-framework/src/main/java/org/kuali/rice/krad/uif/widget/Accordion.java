/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.widget;

/**
 * Decorates a group with collapse/expand functionality
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Accordion extends WidgetBase {
    private static final long serialVersionUID = 1238789480161901850L;

    private String collapseImageSrc;
    private String expandImageSrc;

    private int animationSpeed;
    private boolean defaultOpen;
    private String conditionalDefaultOpen;

    public Accordion() {
        super();

        defaultOpen = true;
    }

    /**
     * Path to the images that should be displayed to collapse the group
     * 
     * @return String image path
     */
    public String getCollapseImageSrc() {
        return this.collapseImageSrc;
    }

    /**
     * Setter for the collapse image path
     * 
     * @param collapseImageSrc
     */
    public void setCollapseImageSrc(String collapseImageSrc) {
        this.collapseImageSrc = collapseImageSrc;
    }

    /**
     * Path to the images that should be displayed to expand the group
     * 
     * @return String image path
     */
    public String getExpandImageSrc() {
        return this.expandImageSrc;
    }

    /**
     * Setter for the expand image path
     * 
     * @param collapseImageSrc
     */
    public void setExpandImageSrc(String expandImageSrc) {
        this.expandImageSrc = expandImageSrc;
    }

    /**
     * Gives the speed for the open/close animation, a smaller int will result
     * in a faster animation
     * 
     * @return int animation speed
     */
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * Setter for the open/close animation speed
     * 
     * @param animationSpeed
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * Indicates whether the group should be initially open
     * 
     * @return boolean true if group should be initially open, false if it
     *         should be closed
     */
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }

    /**
     * Setter for the default open indicator
     * 
     * @param defaultOpen
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

    /**
     * Conditional expression for the default open indicator
     * 
     * @return String should evaluate to boolean
     */
    public String getConditionalDefaultOpen() {
        return this.conditionalDefaultOpen;
    }

    /**
     * Setter for the conditional default open expression
     * 
     * @param conditionalDefaultOpen
     */
    public void setConditionalDefaultOpen(String conditionalDefaultOpen) {
        this.conditionalDefaultOpen = conditionalDefaultOpen;
    }

}

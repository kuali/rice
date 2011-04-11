/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.uif.widget;

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

	public Accordion() {
		super();

		defaultOpen = true;
	}

	public String getCollapseImageSrc() {
		return this.collapseImageSrc;
	}

	public void setCollapseImageSrc(String collapseImageSrc) {
		this.collapseImageSrc = collapseImageSrc;
	}

	public String getExpandImageSrc() {
		return this.expandImageSrc;
	}

	public void setExpandImageSrc(String expandImageSrc) {
		this.expandImageSrc = expandImageSrc;
	}

	public int getAnimationSpeed() {
		return this.animationSpeed;
	}

	public void setAnimationSpeed(int animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	public boolean isDefaultOpen() {
		return this.defaultOpen;
	}

	public void setDefaultOpen(boolean defaultOpen) {
		this.defaultOpen = defaultOpen;
	}

}

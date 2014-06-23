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
package org.kuali.rice.krad.labs.exclusion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Form for demonstrating the use of the {@link Component#getExcludeIf()}
 * and {@link Component#getExcludeUnless()} properties.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsExclusionForm extends UifFormBase {

	private static final long serialVersionUID = 8954792661868833510L;

	private static Random RAND = new Random();

	private static String[] SUBJECTS = new String[] { "dog", "squirrel",
			"cheese", "fruit", "shoes", "dirt", "pizza", "skillet", "soda",
			"shoulder", "demonstration", "truth", "resources", "description",
			"events", "master", "student", "memory", "chef", "spider", };

	private static String[] VERBS = new String[] { "intervene", "believe",
			"pull", "seated", "surface", "outweigh", "sought", "toward",
			"educated", "formed", "surpassed", "comprehend", "astonished",
			"opposed", "creeps", "babble", "simmer", "acquire", "endeavor", };

	private static String[] PREDS = new String[] { "voices", "concern",
			"inflexible", "brief", "crowded", "falsehood", "distraught",
			"astonishment", "another", "decline", "anguish", "superfluous",
			"solace", "silence", "busy", "helplessness", "face", "absence", };

	private List<ExclusionDO> sampleData;

	private boolean showFooColumn;
	private boolean hideBarSection;

	public LabsExclusionForm() {
		sampleData = new ArrayList<ExclusionDO>();
		for (int i = 0; i < 25; i++) {
			ExclusionDO row = new ExclusionDO();
			row.setFoo(SUBJECTS[RAND.nextInt(SUBJECTS.length)]);
			row.setBar(VERBS[RAND.nextInt(VERBS.length)]);
			row.setBaz(PREDS[RAND.nextInt(PREDS.length)]);
			sampleData.add(row);
		}
	}

	/**
	 * Determines if the foo column should be included.
	 * 
	 * @return true of the foo column should be included.
	 */
	public boolean isShowFooColumn() {
		return showFooColumn;
	}

	/**
	 * @see #isShowFooColumn()
	 */
	public void setShowFooColumn(boolean showFooColumn) {
		this.showFooColumn = showFooColumn;
	}

	/**
	 * Determines if the bar section should be excluded.
	 * 
	 * @return true of the bar column should be excluded.
	 */
	public boolean isHideBarSection() {
		return hideBarSection;
	}

	/**
	 * @see #isHideBarSection()
	 */
	public void setHideBarSection(boolean hideBarSection) {
		this.hideBarSection = hideBarSection;
	}

	/**
	 * Gets some random words to fill in the sample data table with.
	 * 
	 * @return some random words
	 */
	public List<ExclusionDO> getSampleData() {
		return sampleData;
	}

}

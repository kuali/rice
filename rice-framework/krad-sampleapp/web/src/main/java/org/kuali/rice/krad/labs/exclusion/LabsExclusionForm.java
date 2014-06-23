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

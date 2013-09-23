package org.kuali.rice.krad.data.provider.annotation;

public enum UifDisplayHintType {
	/**
	 * Tells the defaulter to hide this field and not generate an input field for it.
	 */
	HIDDEN,
	/**
	 * Tells the defaulter to exclude this field and not generate an attribute definition at all.
	 */
	EXCLUDE,
	/**
	 * If a values finder is present for the field, generate as a Drop-down list.
	 */
	DROPDOWN,
	/**
	 * If a values finder is present for the field, generate as a set of radio buttons.
	 */
	RADIO,
	/**
	 * Indicates that this hint tells that the attribute should be put in a specific section.
	 */
	SECTION;
}
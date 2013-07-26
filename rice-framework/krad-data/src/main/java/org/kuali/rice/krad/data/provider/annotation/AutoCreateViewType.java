package org.kuali.rice.krad.data.provider.annotation;

/**
 * Enum representing the types which can be auto-generated.
 */
public enum AutoCreateViewType {
	/**
	 * Convenience value which tells the system to generate all of the other items.
	 */
	ALL,
	/**
	 * Generate a bean which provides an InquiryView
	 */
	INQUIRY,
	/**
	 * Generate a bean which provides a LookupView
	 */
	LOOKUP,
	/**
	 * Generate beans which provides a maintenance document and related MaintenanceView
	 */
	MAINT_DOC;
}

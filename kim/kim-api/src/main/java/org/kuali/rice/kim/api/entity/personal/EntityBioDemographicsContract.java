package org.kuali.rice.kim.api.entity.personal;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.Date;


public interface EntityBioDemographicsContract extends Versioned, GloballyUnique {
    /**
     * Gets this {@link EntityBioDemographicsContract}'s entity id.
     * @return the entity id for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getEntityId();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s deceased date.
     * @return the deceased date for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	Date getDeceasedDate();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s birth date.
     * @return the birth date for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	Date getBirthDate();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s gender code.
     * @return the gender code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getGenderCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s marital status code.
     * @return the marital status code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getMaritalStatusCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s primary language code.
     * @return the primary language code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getPrimaryLanguageCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s secondary language code.
     * @return the secondary language code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getSecondaryLanguageCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s country of birth code.
     * @return the country of birth code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getCountryOfBirthCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s birth state code.
     * @return the birth state code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getBirthStateCode();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s city of birth.
     * @return the city of birth for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getCityOfBirth();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s geographic origin.
     * @return the geographic origin for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getGeographicOrigin();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked birth date.
     * @return the unmasked birth date for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	Date getBirthDateUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked gender code.
     * @return the unmasked gender code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getGenderCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked martial status code.
     * @return the unmasked martial status code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getMaritalStatusCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked primary language code.
     * @return the unmasked primary language code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getPrimaryLanguageCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked secondary language code.
     * @return the unmasked secondary language code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getSecondaryLanguageCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked country of birth code.
     * @return the unmasked country of birth code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getCountryOfBirthCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmaksed birth state code.
     * @return the unmaksed birth state code for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getBirthStateCodeUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked city of birth.
     * @return the unmasked city of birth for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getCityOfBirthUnmasked();

	/**
     * Gets this {@link EntityBioDemographicsContract}'s unmasked geographic origin.
     * @return the unmasked geographic origin for this {@link EntityBioDemographicsContract}, or null if none has been assigned.
     */
	String getGeographicOriginUnmasked();

    /**
     * Returns a boolean value that determines if personal fields should be suppressed.
     * @return boolean value that determines if personal fields should be suppressed.
     */
	boolean isSuppressPersonal();
}

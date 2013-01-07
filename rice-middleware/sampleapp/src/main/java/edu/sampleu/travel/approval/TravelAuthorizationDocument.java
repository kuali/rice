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
package edu.sampleu.travel.approval;

import edu.sampleu.travel.approval.dataobject.PrimaryDestination;
import edu.sampleu.travel.approval.dataobject.TravelerDetail;
import org.kuali.rice.krad.document.TransactionalDocumentBase;

import javax.persistence.*;
import java.util.Date;

/**
 * Travel authorization transactional document.
 *
 * <p>
 *  This is a sample KRAD transactional document that demonstrates how
 *  to implement transactional documents within the KRAD UIF.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_AUTH_DOC_T")
public class TravelAuthorizationDocument extends TransactionalDocumentBase {

    private String travelDocumentIdentifier;
    private String tripTypeCode;
    private Date tripBegin;
    private Date tripEnd;
    private String tripDescription;
    private Boolean primaryDestinationIndicator = false;
    private Integer primaryDestinationId;
    private String primaryDestinationName;
    private String primaryDestinationCountryState;
    private String primaryDestinationCounty;

    // Traveler section
    private Integer travelerDetailId;
    private TravelerDetail travelerDetail;

    private PrimaryDestination primaryDestination;

    public TravelAuthorizationDocument() {
        super();
    }

    /**
     * Returns the travel document identifier.
     *
     * <p>
     * Gets the travel document identifier.
     * </p>
     *
     * @return String - document service
     */
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Initializes the document identifier.
     *
     * <p>
     * Sets the document identifier.
     * </p>
     *
     * @param travelDocumentIdentifier - document identifier
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

    /**
     * Returns the trip type code.
     *
     * <p>
     * Gets the trip type code.
     * </p>
     *
     * @return String - trip type code
     */
    public String getTripTypeCode() {
        return tripTypeCode;
    }

    /**
     * Initializes the trip type code.
     *
     * <p>
     * Sets the trip type code.
     * </p>
     *
     * @param tripTypeCode - trip type code
     */
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    /**
     * Returns the trip begin date.
     *
     * <p>
     * Gets the trip begin date.
     * </p>
     *
     * @return Date - trip begin date
     */
    public Date getTripBegin() {
        return tripBegin;
    }

    /**
     * Initializes the trip starting date.
     *
     * <p>
     * Sets the trip begin date.
     * </p>
     *
     * @param tripBegin - trip starting date
     */
    public void setTripBegin(Date tripBegin) {
        this.tripBegin = tripBegin;
    }

    /**
     * Returns the trip end date.
     *
     * <p>
     * Gets the trip end date.
     * </p>
     *
     * @return Date - trip end date
     */
    public Date getTripEnd() {
        return tripEnd;
    }

    /**
     * Initializes the trip ending date.
     *
     * <p>
     * Sets the trip end date.
     * </p>
     *
     * @param tripEnd - trip ending date
     */
    public void setTripEnd(Date tripEnd) {
        this.tripEnd = tripEnd;
    }

    /**
     * Returns the trip description.
     *
     * <p>
     * Gets the trip description.
     * </p>
     *
     * @return Strin - trip description
     */
    public String getTripDescription() {
        return tripDescription;
    }

    /**
     * Initializes the trip description.
     *
     * <p>
     * Sets the trip description.
     * </p>
     *
     * @param tripDescription- trip description
     */
    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    /**
     * Returns the destination id.
     *
     * <p>
     * Gets the primary key for the destination.
     * </p>
     *
     * @return Integer - destination id
     */
    public Integer getPrimaryDestinationId() {
        return primaryDestinationId;
    }

    /**
     * Initializes the primary destination id.
     *
     * <p>
     * Sets the primary destination id.
     * </p>
     *
     * @param primaryDestinationId - integer of primary destination id
     */
    public void setPrimaryDestinationId(Integer primaryDestinationId) {
        this.primaryDestinationId = primaryDestinationId;
    }

    /**
     * Returns the traveler detail id.
     *
     * <p>
     * Gets the primary key for the traveler.
     * </p>
     *
     * @return Integer - traveler detail id
     */
    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    /**
     * Initializes the traveler detail id.
     *
     * <p>
     * Sets the traveler detail id.
     * </p>
     *
     * @param travelerDetailId - integer of primary destination id
     */
    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    /**
     * Returns the nested traveler detail.
     *
     * <p>
     * Gets the traveler detail object.
     * </p>
     *
     * @return TravelerDetail - traveler detail
     */

    public TravelerDetail getTravelerDetail() {
        return travelerDetail;
    }

    /**
     * Initializes the nested traveler detail object.
     *
     * <p>
     * Sets the traveler detail.
     * </p>
     *
     * @param travelerDetail - traveler detail object
     */
    public void setTravelerDetail(TravelerDetail travelerDetail) {
        this.travelerDetail = travelerDetail;
    }

    /**
     * Returns whether the destination is indicated.
     *
     * <p>
     * Gets the primary destination indicator flag.
     * </p>
     *
     * @return Boolean - primary destination indicator flag
     */
    public Boolean getPrimaryDestinationIndicator() {
        return primaryDestinationIndicator;
    }

    /**
     * Initializes the primary destination flag.
     *
     * <p>
     * Sets the flag whether a primary destination is indicated.
     * </p>
     *
     * @param primaryDestinationIndicator - primary destination indicator
     */
    public void setPrimaryDestinationIndicator(Boolean primaryDestinationIndicator) {
        this.primaryDestinationIndicator = primaryDestinationIndicator;
    }

    /**
     * Returns primary destination name.
     *
     * <p>
     * Gets the name of the primary destination
     * </p>
     *
     * @return String - primary destination name
     */
    public String getPrimaryDestinationName() {
        return primaryDestinationName;
    }

    /**
     * Initializes the primary destination name.
     *
     * <p>
     *    Sets the name for the primary destination.
     * </p>
     *
     * @param primaryDestinationName - primary destination name
     */
    public void setPrimaryDestinationName(String primaryDestinationName) {
        this.primaryDestinationName = primaryDestinationName;
    }

    /**
     * Returns primary destination state.
     *
     * <p>
     * Gets the state of the primary destination
     * </p>
     *
     * @return String - primary destination state
     */
    public String getPrimaryDestinationCountryState() {
        return primaryDestinationCountryState;
    }

    /**
     * Initializes the primary destination state.
     *
     * <p>
     * Sets the state for the primary destination.
     * </p>
     *
     * @param primaryDestinationCountryState - primary destination state
     */
    public void setPrimaryDestinationCountryState(String primaryDestinationCountryState) {
        this.primaryDestinationCountryState = primaryDestinationCountryState;
    }

    /**
     * Returns primary destination county.
     *
     * <p>
     * Gets the county of the primary destination
     * </p>
     *
     * @return String - primary destination county
     */
    public String getPrimaryDestinationCounty() {
        return primaryDestinationCounty;
    }

    /**
     * Initializes the primary destination county.
     *
     * <p>
     * Sets the county for the primary destination.
     * </p>
     *
     * @param primaryDestinationCounty - primary destination county
     */
    public void setPrimaryDestinationCounty(String primaryDestinationCounty) {
        this.primaryDestinationCounty = primaryDestinationCounty;
    }

    /**
     * Returns primary destination.
     *
     * <p>
     * Gets the primary destination
     * </p>
     *
     * @return PrimaryDestination - primary destination
     */
    public PrimaryDestination getPrimaryDestination() {
        return primaryDestination;
    }

    /**
     * Initializes the primary destination.
     *
     * <p>
     * Sets the primary destination.
     * </p>
     *
     * @param primaryDestination - primary destination
     */
    public void setPrimaryDestination(PrimaryDestination primaryDestination) {
        this.primaryDestination = primaryDestination;
    }
}

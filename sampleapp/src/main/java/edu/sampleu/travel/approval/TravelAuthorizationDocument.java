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
import edu.sampleu.travel.approval.dataobject.TravelAdvance;
import org.kuali.rice.krad.document.TransactionalDocumentBase;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    private Date tripBegin;
    private Date tripEnd;
    private String tripDescription;
    private String tripTypeCode;

    // Traveler section
    private Integer travelerDetailId;
    private TravelerDetail travelerDetail;

    // Primary Destination section
    private Integer primaryDestinationId;
    private PrimaryDestination primaryDestination;
    
    // Travel Advance 
    private List<TravelAdvance> travelAdvanceList ;

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
     * Initializes the trip type.
     *
     * <p>
     * Sets the trip type.
     * </p>
     *
     * @param tripTypeCode - trip type
     */
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    /**
     * Returns the trip type.
     *
     * <p>
     * Gets the trip type.
     * </p>
     *
     * @return String - trip type
     */
    public String getTripTypeCode() {
        return tripTypeCode;
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

    /**
     * Returns travel advance collection.
     *
     * <p>
     * Gets the travel advance collection.
     * </p>
     *
     * @return List<TravelAdvance> - travel advance collection
     */
    public List<TravelAdvance> getTravelAdvanceList() {
        return travelAdvanceList;
    }

    /**
     * Initializes travel advance collection.
     *
     * <p>
     * Sets the travel advance collection.
     * </p>
     *
     * @param travelAdvanceList - travel advance collection
     */
    public void setTravelAdvanceList(List<TravelAdvance> travelAdvanceList) {
        this.travelAdvanceList = travelAdvanceList;
    }





}

/*
 * Copyright 2012 The Kuali Foundation.
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
package edu.arizona.kim.eds;

import java.util.SortedSet;
import java.util.TreeSet;

/*
 * A class to describe an EDS record for a person. The non-bean like fields are those for affiliations,
 * employeeStatus, employeCode, deptCode, and deptName. The collection of affiliations are ordered
 * by an AffiliationComparator, which orders each affiliation such that the position of the
 * affiliation is by privilege/rank. For example, affiliations that are active precede those that
 * are not. Affiliations that are active and 'respected' are positioned before affiliations
 * that are active and 'unrespected'. Thus, the affiliation at position zero, is the 'best'
 * affiliation, the most privileged. So employeeStatus, employeCode, deptCode, and deptName
 * simply come from the affiliation at position zero. It is guarunteed that if an active
 * affiliation present, it will be in position zero. Also, if a respected and active affiliation
 * is present, it is guarunteed to be in position zero. See the AffiliationComparator for more
 * detail. 
 */
public class UaEdsRecord {

	// Instance Variables
	private String uaId;
	private String uid; // aka NetID
	private String emplId; // PeopleSoft ID, even students get one
	private String givenName;
	private String cn;
	private String sn;
	private String mail;
	private String employeePoBox;
	private String employeeCity;
	private String employeeState;
	private String employeeZip;
	private SortedSet<UaEdsAffiliation> orderedAffiliations;
	boolean isActive;

	public UaEdsRecord(UaAffiliationComparator affComparator) {
		orderedAffiliations = new TreeSet<UaEdsAffiliation>(affComparator);
		isActive = false;
	}

	public String getUaId() {
		return uaId;
	}

	public void setUaId(String uaId) {
		this.uaId = uaId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmplId() {
		return emplId;
	}

	public void setEmplId(String employeeId) {
		this.emplId = employeeId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public SortedSet<UaEdsAffiliation> getOrderedAffiliations() {
		return orderedAffiliations;
	}

	public void addAffiliation(UaEdsAffiliation edsAffiliation) {
		orderedAffiliations.add(edsAffiliation);
	}

	public void clearSortedAffiliations() {
		orderedAffiliations.clear();
	}

	public String getStatusCode() {
		return orderedAffiliations.isEmpty() ? null : orderedAffiliations.first().getStatusCode();
	}

	public String getEmployeeType() {
		return orderedAffiliations.isEmpty() ? null : orderedAffiliations.first().getEmployeeType();
	}

	/**
	 * As of release 58 these values include:
	 *  A = Classified Staff Wage
	 *  D = Ancillary Staff Wage 
	 *  E = Regular Appointed Acad 
	 *  F = Regular Appointed Temp 
	 *  H = Ancillary Appointed Temp 
	 *  G = Ancillary Appointed Academic 
	 *  I = Supplemental Comp 
	 *  J = High School Students 
	 *  P = Contingent Worker 
	 *  L = Limited Term Adjunct Lecturer 
	 *  U = Unknown 
	 *  Z = Other Professional Service
	 *  X = Extra Help - Faculty 
	 *  1 = Student Employees 
	 *  2 = Classified Staff Salary 
	 *  3 = Ancillary Staff Salary 
	 *  4 = Regular Appointed Fiscal 
	 *  5 = Clinical Faculty 
	 *  6 = Federal Employees 
	 *  7 = Ancillary Appointed Fiscal 
	 *  8 = Graduate Assistants/Associates
	 */
	public String getEmployeeTypeCode() {
		return orderedAffiliations.isEmpty() ? null : orderedAffiliations.first().getEmployeeType();
	}

	// The AffiliationComparator on sortedAffiliations ensures the best
	// affiliation is always first
	public UaEdsAffiliation getBestAffiliation() {
		return orderedAffiliations.isEmpty() ? null : orderedAffiliations.first();
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getEmployeePoBox() {
		return employeePoBox;
	}

	public void setEmployeePoBox(String employeePoBox) {
		this.employeePoBox = employeePoBox;
	}

	public String getEmployeeCity() {
		return employeeCity;
	}

	public void setEmployeeCity(String employeeCity) {
		this.employeeCity = employeeCity;
	}

	public String getEmployeeState() {
		return employeeState;
	}

	public void setEmployeeState(String employeeState) {
		this.employeeState = employeeState;
	}

	public String getEmployeeZip() {
		return employeeZip;
	}

	public void setEmployeeZip(String employeeZip) {
		this.employeeZip = employeeZip;
	}

}

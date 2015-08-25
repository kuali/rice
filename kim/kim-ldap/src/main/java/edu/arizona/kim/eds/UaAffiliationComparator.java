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

import java.util.Comparator;
import java.util.List;

public class UaAffiliationComparator implements Comparator<UaEdsAffiliation> {

	@SuppressWarnings("unused")
	private List<String> sortedActiveIndicatorStrings;
	private List<String> sortedAffiliationStrings;

	public UaAffiliationComparator(List<String> sortedAffiliationStrings, List<String> sortedActiveIndicatorStrings) {
		super();
		this.sortedAffiliationStrings = sortedAffiliationStrings;
		this.sortedActiveIndicatorStrings = sortedActiveIndicatorStrings;
	}

	@Override
	public int compare(UaEdsAffiliation firstAff, UaEdsAffiliation secondAff) {

		if (firstAff.isActive() && !secondAff.isActive()) {
			return -1;
		} else if (!firstAff.isActive() && secondAff.isActive()) {
			return 1;
		}

		// If we made it here, the statuses are equal, so use the order of
		// affiliation as the deciding factor
		Integer firstAffPos = Integer.MAX_VALUE;
		Integer secondAffPos = Integer.MAX_VALUE;
		for (int i = 0; i < sortedAffiliationStrings.size(); i++) {
			String currAffString = sortedAffiliationStrings.get(i);
			if (firstAff.getAffiliatonString().equals(currAffString)) {
				firstAffPos = i;
			}
			if (secondAff.getAffiliatonString().equals(currAffString)) {
				secondAffPos = i;
			}
			if (firstAffPos.compareTo(secondAffPos) != 0) {
				return firstAffPos.compareTo(secondAffPos);
			}
		}

		// If we make it here, active indicator and status are both equal,
		// so just use department code as the final decider
		if (firstAff.getDeptCode() != null && secondAff.getDeptCode() == null) {
			return -1;
		} else if (firstAff.getDeptCode() == null && secondAff.getDeptCode() != null) {
			return 1;
		} else if ((firstAff.getDeptCode() == null && secondAff.getDeptCode() == null)) {
			return 0;
		} else {
			return firstAff.getDeptCode().compareTo(secondAff.getDeptCode());
		}

	}

}

/*
 * Copyright 2005-2009 The Kuali Foundation
 *
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
package mocks;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockChartOrgService {

	private static List<MockOrganization> orgs = new ArrayList<MockOrganization>();
	static {
		orgs.add(new MockOrganization("IU", "UNIV", "IU", "UNIV"));
		orgs.add(new MockOrganization("BL", "BL", "IU", "UNIV"));
		orgs.add(new MockOrganization("BL", "ARSC", "BL", "BL"));
		orgs.add(new MockOrganization("BL", "CHEM", "BL", "ARSC"));
		orgs.add(new MockOrganization("KO", "KO", "IU", "UNIV"));
		orgs.add(new MockOrganization("IN", "IN", "IU", "UNIV"));
		orgs.add(new MockOrganization("IN", "MED", "IU", "UNIV"));
	}

	public MockOrganization findOrganization(String chart, String org) {
		for (MockOrganization organization : orgs) {
			if (chart.equals(organization.getFinCoaCd()) && org.equals(organization.getOrgCd())) {
				return organization;
			}
		}
		return null;
	}

}

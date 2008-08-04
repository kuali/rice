/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.workgroup;

import java.util.ArrayList;
import java.util.List;

import org.kuali.workflow.attribute.ExtensionAttribute;

import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.validation.ValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ChartOrgDataAttribute implements ExtensionAttribute {

	private static final String CHART = "chartOfAccountsCode";
	private static final String ORG = "organizationCode";

	private List<Row> rows;

	public ChartOrgDataAttribute() {
        rows = new ArrayList<Row>();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, true, CHART, "", null, "ChartOrgLookupableImplService", "fin_coa_cd"));
        rows.add(new Row(fields, "Chart & Org", 2));

        fields = new ArrayList();
        fields.add(new Field("Org", "", Field.TEXT, true, ORG, "", null, "ChartOrgLookupableImplService", "org_cd"));
        fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, "ChartOrgLookupableImplService"));
        rows.add(new Row(fields, "Chart & Org", 2));

	}

	public List<Row> getRows() {
		return rows;
	}

	public ValidationResults validate(ValidationContext validationContext) {
		return null;
	}

}

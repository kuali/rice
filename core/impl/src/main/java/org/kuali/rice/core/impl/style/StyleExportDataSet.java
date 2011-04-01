/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.impl.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.impex.ExportDataSet;

/**
 * TODO
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StyleExportDataSet {

	public static final QName STYLES = new QName("CORE", "styles");
	
	private List<StyleBo> styles = new ArrayList<StyleBo>();

	public List<StyleBo> getStyles() {
		return styles;
	}
	
	public void populateExportDataSet(ExportDataSet exportDataSet) {
		if (styles != null && !styles.isEmpty()) {
			exportDataSet.addDataSet(STYLES, styles);
		}
	}
	
	public ExportDataSet createExportDataSet() {
		ExportDataSet exportDataSet = new ExportDataSet();
		populateExportDataSet(exportDataSet);
		return exportDataSet;
	}
	
	public static StyleExportDataSet fromExportDataSet(ExportDataSet exportDataSet) {
		StyleExportDataSet coreExportDataSet = new StyleExportDataSet();
		
		List<StyleBo> styles = (List<StyleBo>)exportDataSet.getDataSets().get(STYLES);
		if (styles != null) {
			coreExportDataSet.getStyles().addAll(styles);
		}
		
		return coreExportDataSet;
	}
	
}

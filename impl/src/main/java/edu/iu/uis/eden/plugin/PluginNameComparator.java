/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.plugin;

import java.util.Comparator;

/**
 * A comparator which sorts a collection of plugins names alphabeticaly, putting the institutional plugin first.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginNameComparator implements Comparator<String> {

	private final String institutionalPluginName;
	
	public PluginNameComparator(String institutionalPluginName) {
		this.institutionalPluginName = institutionalPluginName;
	}
	
    public int compare(String pluginName1, String pluginName2) {
        int compareValue = 0;
        if (pluginName1.equals(pluginName2)) {
            compareValue = 0;
        } else if (institutionalPluginName.equals(pluginName1)) {
            compareValue = -1;
        } else if (institutionalPluginName.equals(pluginName2)) {
            compareValue = 1;
        } else {
            compareValue = pluginName1.compareTo(pluginName2);
        }
        return compareValue;
    }

    public String getInstitutionalPluginName() {
    	return institutionalPluginName;
    }

}

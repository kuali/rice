
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
 */package edu.iu.uis.eden.database.platform;



public abstract class ANSISqlPlatform implements Platform {

	public String getDateSQL(String date, String time) {
        // SQL 92 date literal syntax:
        // http://www.stanford.edu/dept/itss/docs/oracle/9i/java.920/a96654/ref.htm#1005145
        String d = date.replace('/', '-');
        if (time == null) {
            return "{d '" + d + "'}";    
        } else {
            return "{ts '" + d + " " + time + "'}"; 
        }
    }

    public String toString() {
        return "[ANSISqlPlatform]";
    }
}
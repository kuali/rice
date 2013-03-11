/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.sampleu.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestSuggestClass {

    public static List<String> getLanguages(String term) {
        List<String> matchingLanguages = new ArrayList<String>();

        String[] languageArray =
                {"ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang",
                        "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby",
                        "Scala", "Scheme"};

        for (int i = 0; i < languageArray.length; i++) {
            String language = languageArray[i];
            if (language.toLowerCase().startsWith(term.toLowerCase())) {
                matchingLanguages.add(language);
            }
        }

        return matchingLanguages;
    }

    public static List<String> getAllLanguages() {
        String[] languageArray =
                {"ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang",
                        "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby",
                        "Scala", "Scheme"};

        return Arrays.asList(languageArray);
    }

    public static List<TestLabelValue> getRichOptions() {
        List<TestLabelValue> options = new ArrayList<TestLabelValue>();

        options.add(new TestLabelValue("r1", "<b>Rich Option 1</b><br/><i>this is a desc for option 1</i>"));
        options.add(new TestLabelValue("r1", "<b>Rich Option 2</b><br/><i>this is a desc for option 2</i>"));
        options.add(new TestLabelValue("r1", "<b>Rich Option 3</b><br/><i>this is a desc for option 3</i>"));

        return options;
    }

    public static List<TestSuggestObject> getComplexOptions() {
        List<TestSuggestObject> options = new ArrayList<TestSuggestObject>();

        options.add(new TestSuggestObject("1", "jhbon", "Bohan, Jack"));
        options.add(new TestSuggestObject("2", "jmcross", "Cross, Jeff"));
        options.add(new TestSuggestObject("3", "jomot", "Mot, Joe"));

        return options;
    }

    public static class TestLabelValue {
        private String label;
        private String value;

        public TestLabelValue() {

        }

        public TestLabelValue(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
     public static class TestSuggestObject extends TestLabelValue {
         private String description;
         
         public TestSuggestObject(String value, String label, String description) {
             super(value, label);
             this.description = description;
         }

         public String getDescription() {
             return description;
         }

         public void setDescription(String description) {
             this.description = description;
         }
     }

}

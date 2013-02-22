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
package org.kuali.rice.krad.demo.uif.components;

import java.util.ArrayList;
import java.util.List;

public class ComponentSuggestClass {
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
}

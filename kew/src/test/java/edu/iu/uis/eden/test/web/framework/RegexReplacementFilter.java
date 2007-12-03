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
package edu.iu.uis.eden.test.web.framework;

import java.util.regex.Pattern;

/**
 * Filter implementation that uses a regular expression
 * search/replace
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RegexReplacementFilter implements Filter {
    private Pattern pattern;
    private String replacement;

    public RegexReplacementFilter(String pattern, String replacement) {
        this(Pattern.compile(pattern), replacement);
    }

    public RegexReplacementFilter(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public String filter(String value) {
        return pattern.matcher(value).replaceAll(replacement);
    }

    public String toString() {
        return pattern.pattern() + " / '" + replacement + "'";
    }
}
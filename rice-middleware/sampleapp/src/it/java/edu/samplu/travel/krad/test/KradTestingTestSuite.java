/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.samplu.travel.krad.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.samplu.travel.krad.test.CollectionTotallingNavIT;
import edu.samplu.travel.krad.test.CollectionTotallingWDIT;
import edu.samplu.travel.krad.test.ConfigurationTestViewNavIT;
import edu.samplu.travel.krad.test.ConfigurationTestViewWDIT;
import edu.samplu.travel.krad.test.DirtyFieldsCheckWDIT;
import edu.samplu.travel.krad.test.UIFComponentValidationRegexPatternNavIT;
import edu.samplu.travel.krad.test.UIFComponentValidationRegexPatternWDIT;
import edu.samplu.travel.krad.test.UifDataAttributesNavIT;
import edu.samplu.travel.krad.test.WatermarkValidationIT;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(Suite.class)
@SuiteClasses({
        CollectionTotallingNavIT.class,
        CollectionTotallingWDIT.class,
        ConfigurationTestViewNavIT.class,
        ConfigurationTestViewWDIT.class,
        DirtyFieldsCheckWDIT.class,
        UIFComponentValidationRegexPatternNavIT.class,
        UIFComponentValidationRegexPatternWDIT.class,
        UifDataAttributesNavIT.class,
        WatermarkValidationIT.class})
public class KradTestingTestSuite {

}

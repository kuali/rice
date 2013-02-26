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
package edu.samplu.krad.validationmessagesview;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(Suite.class)
@SuiteClasses({
    ClientErrorsNavIT.class,
    ClientErrorsWDIT.class,
    ServerErrorsNavIT.class,
    ServerErrorsWDIT.class,
    ServerWarningsNavIT.class,
    ServerWarningsWDIT.class,
    ServerInfoNavIT.class,
    ServerInfoWDIT.class
})
public class ValidateErrorsWarningsTestSuite {

}

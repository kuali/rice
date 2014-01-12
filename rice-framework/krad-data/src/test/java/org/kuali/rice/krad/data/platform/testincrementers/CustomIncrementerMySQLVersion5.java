/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.platform.testincrementers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;

import javax.sql.DataSource;

/**
 * Mock incrementer used to test custom incrementers
 */
 public class CustomIncrementerMySQLVersion5 extends AbstractColumnMaxValueIncrementer {

    @Override
        public int nextIntValue() throws DataAccessException {
            return 0;
        }

        @Override
        public long nextLongValue() throws DataAccessException {
            return 0;
        }

        @Override
        public String nextStringValue() throws DataAccessException {
            return "mySQL5";
        }

        @Override
        protected long getNextKey() {
            return 5555L;
        }
}

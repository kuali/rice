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
package org.kuali.rice.sql.spring;

import org.kuali.common.jdbc.reset.JdbcResetExecConfig;
import org.kuali.common.util.execute.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Central configuration file for launching the database reset process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ SourceSqlConfig.class, JdbcResetExecConfig.class })
public class SourceSqlExecConfig {

    /**
     * The JDBC reset database configuration.
     */
    @Autowired
    JdbcResetExecConfig config;

    /**
     * Returns the executable for launching the database reset process.
     *
     * @return the executable for launching the database reset process
     */
    @Bean
    public Executable sourceSqlExecutable() {
        return config.executable();
    }

}
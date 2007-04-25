/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.PropertyConfigurator;
import org.kuali.Constants;
import org.kuali.core.util.log4j.StartupTimeStatsMailAppender;

public class Log4jConfigurer {
    private static final long MILLISECONDS_CONVERSION_MULTIPLIER = 60 * 1000;
    private static StartupTimeStatsMailAppender NDC_APPENDER = new StartupTimeStatsMailAppender(new Date().getTime());

    public static final void configureLogging() {
        String settingsFile = ResourceBundle.getBundle(Constants.CONFIGURATION_FILE_NAME).getString(Constants.LOG4J_SETTINGS_FILE_KEY);
        String reloadMinutes = ResourceBundle.getBundle(Constants.CONFIGURATION_FILE_NAME).getString(Constants.LOG4J_RELOAD_MINUTES_KEY);
        long reloadMilliseconds = 5 * MILLISECONDS_CONVERSION_MULTIPLIER;
        try {
            reloadMilliseconds = Long.parseLong(reloadMinutes) * MILLISECONDS_CONVERSION_MULTIPLIER;
        }
        catch (NumberFormatException ignored) {
            // default to 5 minutes
        }
        PropertyConfigurator.configureAndWatch(settingsFile, reloadMilliseconds);
        Logger.getRootLogger().addAppender(NDC_APPENDER);
        setStartupNdc();
    }

    public static void setStartupNdc() {
        NDC.push(StartupTimeStatsMailAppender.STARTUP_NDC_INFO);
    }

    public static void completeStartupLogging() {
        NDC_APPENDER.close();
    }
}
/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.web.format;


/**
 * This class is used to format Timestamp objects with full precision.
 */
public class TimestampFullPrecisionFormatter extends TimestampFormatterBase {
    // If you change this String, the app will break. At least, it will until we change
    // DocumentNote and DocumentAttachment so their primary key uses something simple
    // like a sequence number (or even the long version of a timestamp) instead of using
    // a timestamp
    private static final String FORMAT = "MM/dd/yyyy HH:mm:ss";

    public TimestampFullPrecisionFormatter() {
        super(FORMAT);
    }
}

/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.export;

/**
 * A type-safe enumeration representing a format for data export.  Currently the only
 * implemented format is XML.
 * 
 * @see ExportDataSet
 * @see Exporter
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExportFormat {

    private static final String XML_FORMAT = "XML";
    private static final String XML_EXT = ".xml";
    private static final String XML_MIME = "application/xml";
    
    public static final ExportFormat XML = new ExportFormat(XML_FORMAT, XML_EXT, XML_MIME);
    
    private final String formatName;
    private final String extension;
    private final String mimeType;
    
    private ExportFormat(String formatName, String extension, String mimeType) {
        this.formatName = formatName;
        this.extension = extension;
        this.mimeType = mimeType;
    }
    
    public String getFormatName() {
        return formatName;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public static ExportFormat getFormatForName(String formatName) {
        if (formatName.equals(XML.getFormatName())) {
            return XML;
        }
        return null;
    }
    
    public boolean equals(Object object) {
        if (object instanceof ExportFormat) {
            return formatName.equals(((ExportFormat)object).formatName);
        }
        return false;
    }
    
    public int hashCode() {
        return formatName.hashCode();
    }
    
    public String toString() {
        return formatName;
    }

}

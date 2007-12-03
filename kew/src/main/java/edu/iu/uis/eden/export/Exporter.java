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

import java.util.List;

/**
 * Interface for an exporter which can export an {@link ExportDataSet} as an array of bytes
 * in the given {@link ExportFormat}.
 *
 * @see ExportDataSet
 * @see ExportFormat
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Exporter {

    /**
     * Returns a List of supported ExportFormats in which this Exporter's data can be exported.
     */
    public List getSupportedFormats();
    
    /**
     * Initiates the export of the given data in the specified format.  The format passed in should be 
     * one of the supported formats as specified by the getSupportedFormats() method.  The data also
     * needs to be understood by the exporter implementation.  Will throw an ExportNotSupportedException
     * if there is a error in regards to the export cabilities of the exporter for the given
     * parameters.  Returns the raw exported data as bytes.
     */
    public byte[] export(ExportFormat format, ExportDataSet dataSet);
    
}

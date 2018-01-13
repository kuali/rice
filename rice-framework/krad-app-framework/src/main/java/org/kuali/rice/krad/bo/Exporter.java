/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import org.kuali.rice.krad.exception.ExportNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * An Exporter provides the ability to export a list of data objects to a supported export format.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Exporter {

    /**
     * Exports the List of BusinessObjects to the specified ExportFormat.
     *
     * <p>The resulting output of the export operation should be written to the given
     * OutputStream</p>
     *
     * @param dataObjectClass the type of DataObjects being exported
     * @param dataObjects a List of DataObjects to export
     * @param exportFormat the export format in which to export the DataObjects
     * @param outputStream the OutputStream to write the exported data to
     * @throws IOException if the process encounters an I/O issue
     * @throws ExportNotSupportedException if the given ExportFormat is not supported
     */
    void export(Class<?> dataObjectClass, List<? extends Object> dataObjects, String exportFormat,
            OutputStream outputStream) throws IOException, ExportNotSupportedException;

    /**
     * Returns a List of ExportFormats supported by this Exporter for the given DataOject class.
     *
     * @param dataObjectClass the class of the DataObjects being exported
     * @return a List of supported ExportFormats
     */
    List<String> getSupportedFormats(Class<?> dataObjectClass);

}

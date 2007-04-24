/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.service;

import java.util.Set;

import org.kuali.core.bo.PersistableBusinessObject;

public interface DatabaseImportExportService {
    public static final String DDL_FILE_SUFFIX = ".ddl";
    public static final String TABLE_FILE_SUFFIX = ".table" + DDL_FILE_SUFFIX;
    public static final String SEQUENCE_FILE_SUFFIX = ".sequence" + DDL_FILE_SUFFIX;
    public static final String INDEX_FILE_SUFFIX = ".index" + DDL_FILE_SUFFIX;
    public static final String VIEW_FILE_SUFFIX = ".view" + DDL_FILE_SUFFIX;
    public static final String FUNCTION_FILE_SUFFIX = ".function" + DDL_FILE_SUFFIX;
    public static final String PROCEDURE_FILE_SUFFIX = ".procedure" + DDL_FILE_SUFFIX;
    public static final String DUMP_ZIP_FILE_SUFFIX = ".zip";
    public static final String DUMP_FILE_SUFFIX = ".dump";
    public static final String SQL_FILE_SUFFIX = ".sql";
    public static final int BUFFER_SIZE = 2048;

    public void purgeDatabase();
    
    public void truncateSequenceTables();
    
    public void processSqlFile(String fileName);

    public void processDumpFile(String fileName);

    public void exportData(String exportDirectory);

    public void checkArguments(Class businessObjectClass, Set<String> attributeNames);

    public void createBackupTable(Class businessObjectClass);

    public void prepClassDescriptor(Class businessObjectClass, Set<String> attributeNames);

    public void truncateTable(Class businessObjectClass);

    public void encrypt(PersistableBusinessObject businessObject, Set<String> attributeNames);

    public void restoreClassDescriptor(Class businessObjectClass, Set<String> attributeNames);

    public void restoreTableFromBackup(Class businessObjectClass);

    public void dropBackupTable(Class businessObjectClass);
    
    public void updateWorkflowDocHandlerUrls();
}
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
package edu.iu.uis.eden.stats;

import java.sql.SQLException;
import java.util.Date;

import org.apache.ojb.broker.accesslayer.LookupException;

/**
 * A service for obtaining various pieces of statistics information about the
 * KEW application.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface StatsService {

    public void ActionsTakenPerUnitOfTimeReport(Stats stats, Date begDate, Date endDate, String unitOfTimeConst) throws SQLException, LookupException;
    public void NumActiveItemsReport(Stats stats) throws SQLException, LookupException;
    public void DocumentsRoutedReport(Stats stats, Date begDate, Date endDate) throws SQLException, LookupException;
    public void NumberOfDocTypesReport(Stats stats) throws SQLException, LookupException;
    public void NumUsersReport(Stats stats) throws SQLException, LookupException;    
    public void NumInitiatedDocsByDocTypeReport(Stats stats) throws SQLException, LookupException;
    
}

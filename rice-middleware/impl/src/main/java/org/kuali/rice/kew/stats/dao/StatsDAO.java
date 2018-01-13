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
package org.kuali.rice.kew.stats.dao;

import java.sql.SQLException;
import java.util.Date;

import org.kuali.rice.kew.stats.Stats;


public interface StatsDAO {

    public void NumActiveItemsReport(Stats stats) throws SQLException;
    public void DocumentsRoutedReport(Stats stats, Date begDate, Date endDate) throws SQLException;
    public void NumberOfDocTypesReport(Stats stats) throws SQLException;
    public void NumUsersReport(Stats stats) throws SQLException;
    public void NumInitiatedDocsByDocTypeReport(Stats stats) throws SQLException;

}

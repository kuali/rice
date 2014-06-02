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
package org.kuali.rice.kew.stats.service.impl;

import java.sql.SQLException;
import java.util.Date;

import org.kuali.rice.kew.stats.Stats;
import org.kuali.rice.kew.stats.dao.StatsDAO;
import org.kuali.rice.kew.stats.service.StatsService;


public class StatsServiceImpl implements StatsService {

    private StatsDAO statsDAO;

    @Override
    public void NumActiveItemsReport(Stats stats) throws SQLException {
        getStatsDAO().NumActiveItemsReport(stats);
    }

    @Override
    public void DocumentsRoutedReport(Stats stats, Date begDate, Date endDate) throws SQLException {
        getStatsDAO().DocumentsRoutedReport(stats, begDate, endDate);
    }

    @Override
    public void NumberOfDocTypesReport(Stats stats) throws SQLException {
        getStatsDAO().NumberOfDocTypesReport(stats);
    }

    @Override
    public void NumUsersReport(Stats stats) throws SQLException {
        getStatsDAO().NumUsersReport(stats);
    }

    @Override
    public void NumInitiatedDocsByDocTypeReport(Stats stats) throws SQLException {
        getStatsDAO().NumInitiatedDocsByDocTypeReport(stats);
    }

    public StatsDAO getStatsDAO() {
        return statsDAO;
    }

    public void setStatsDAO(StatsDAO statsDAO) {
        this.statsDAO = statsDAO;
    }
}

/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.stats.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.stats.Stats;
import edu.iu.uis.eden.web.KeyValue;

public class StatsDAOOjbImpl extends PersistenceBrokerDaoSupport implements StatsDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StatsDAOOjbImpl.class);

    public static final String SQL_NUM_ACTION_PER_TIME = "select " + "round(avg(count(to_char(actn_tkn_dt, ?)))) as avg from en_actn_tkn_t where actn_tkn_dt between ? and ? group by to_char(actn_tkn_dt, ?)";
    public static final String SQL_NUM_ACTIVE_ITEMS = "select count(*) from en_actn_itm_t";
    public static final String SQL_NUM_DOC_TYPES_REPORT = "select count(*) as num from en_doc_typ_t where doc_typ_cur_ind = 1";
    public static final String SQL_DOCUMENTS_ROUTED = "select count(*) as count, en_doc_hdr_t.doc_rte_stat_cd from en_doc_hdr_t where en_doc_hdr_t.doc_crte_dt between ? and ? group by doc_rte_stat_cd";
    public static final String SQL_NUM_USERS = "select count(distinct prsn_en_id) as prsn_count from en_usr_optn_t";
    public static final String SQL_NUM_DOCS_INITIATED = "select count(*), en_doc_typ_t.doc_typ_nm from en_doc_hdr_t, en_doc_typ_t where en_doc_hdr_t.doc_crte_dt > ? and en_doc_hdr_t.doc_typ_id = en_doc_typ_t.doc_typ_id group by en_doc_typ_t.doc_typ_nm";
    
    
    
    public void ActionsTakenPerUnitOfTimeReport(Stats stats, Date begDate, Date endDate, String unitOfTimeConst) throws SQLException, LookupException {

        LOG.debug("ActionsTakenPerUnitOfTimeReport()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_NUM_ACTION_PER_TIME);
        ps.setString(1, unitOfTimeConst);
        ps.setTimestamp(2, new Timestamp(begDate.getTime()));
        ps.setTimestamp(3, new Timestamp(endDate.getTime()));
        ps.setString(4, unitOfTimeConst);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            stats.setAvgActionsPerDoc(new Integer(rs.getInt("avg")).toString());
        }

        closeDatabaseObjects(rs, ps, conn, broker);
    }

    public void NumActiveItemsReport(Stats stats) throws SQLException, LookupException {

        LOG.debug("NumActiveItemsReport()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_NUM_ACTIVE_ITEMS);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            stats.setNumActionItems(new Integer(rs.getInt(1)).toString());
        }

        closeDatabaseObjects(rs, ps, conn, broker);
    }

    public void NumberOfDocTypesReport(Stats stats) throws SQLException, LookupException {

        LOG.debug("NumberOfDocTypesReport()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_NUM_DOC_TYPES_REPORT);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            stats.setNumDocTypes(new Integer(rs.getInt(1)).toString());
        }

        closeDatabaseObjects(rs, ps, conn, broker);
    }

    public void DocumentsRoutedReport(Stats stats, Date begDate, Date endDate) throws SQLException, LookupException {

        LOG.debug("DocumentsRoutedReport()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_DOCUMENTS_ROUTED);
        ps.setTimestamp(1, new Timestamp(begDate.getTime()));
        ps.setTimestamp(2, new Timestamp(endDate.getTime()));
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            String actionType = rs.getString(2);
            String number = new Integer(rs.getInt(1)).toString();
            if (actionType.equals(EdenConstants.ROUTE_HEADER_APPROVED_CD)) {
                stats.setApprovedNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_CANCEL_CD)) {
                stats.setCanceledNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_DISAPPROVED_CD)) {
                stats.setDisapprovedNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_ENROUTE_CD)) {
                stats.setEnrouteNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_EXCEPTION_CD)) {
                stats.setExceptionNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_FINAL_CD)) {
                stats.setFinalNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_INITIATED_CD)) {
                stats.setInitiatedNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_PROCESSED_CD)) {
                stats.setProcessedNumber(number);
            } else if (actionType.equals(EdenConstants.ROUTE_HEADER_SAVED_CD)) {
                stats.setSavedNumber(number);
            }
        }

        closeDatabaseObjects(rs, ps, conn, broker);
    }

    public void NumUsersReport(Stats stats) throws SQLException, LookupException {

        LOG.debug("NumUsersReport()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_NUM_USERS);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            stats.setNumUsers(new Integer(rs.getInt("prsn_count")).toString());
        }

        closeDatabaseObjects(rs, ps, conn, broker);
    }

    public void NumInitiatedDocsByDocTypeReport(Stats stats) throws SQLException, LookupException {
        
        LOG.debug("NumInitiatedDocsByDocType()");
        PersistenceBroker broker = this.getPersistenceBroker(false);
        Connection conn = broker.serviceConnectionManager().getConnection();  
        PreparedStatement ps = conn.prepareStatement(StatsDAOOjbImpl.SQL_NUM_DOCS_INITIATED);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -29);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);        
        ps.setTimestamp(1, new Timestamp(calendar.getTime().getTime()));
        ResultSet rs = ps.executeQuery();

        List numDocs = new ArrayList();
        
        while (rs.next()) {
            numDocs.add(new KeyValue(rs.getString(2), new Integer(rs.getInt(1)).toString()));
        }
        stats.setNumInitiatedDocsByDocType(numDocs);
        
        closeDatabaseObjects(rs, ps, conn, broker);        
        
    }
   
    private void closeDatabaseObjects(ResultSet rs, PreparedStatement ps, Connection conn, PersistenceBroker broker) {

        try {
            rs.close();
        } catch (SQLException ex) {
            LOG.warn("Failed to close ResultSet.", ex);
        }

        try {
            ps.close();
        } catch (SQLException ex) {
            LOG.warn("Failed to close PreparedStatement.", ex);
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            LOG.warn("Failed to close Connection.", ex);
        }        

        try {
            broker.close();
        } catch (Exception ex) {
            LOG.warn("Failed to close broker.", ex);
        }     
    
    }
    
}
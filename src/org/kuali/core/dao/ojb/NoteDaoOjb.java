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
package org.kuali.core.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.Attachment;
import org.kuali.core.bo.Note;
import org.kuali.core.dao.NoteDao;
import org.springframework.dao.DataAccessException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the NoteDao interface.
 *
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NoteDaoOjb extends PlatformAwareDaoBaseOjb implements NoteDao {
    private static Logger LOG = Logger.getLogger(NoteDaoOjb.class);

    /**
     * Default constructor.
     */
    public NoteDaoOjb() {
        super();
    }

    /**
     * Saves a note to the DB using OJB.
     *
     * @param line
     */
    public void save(Note note) throws DataAccessException {
        //workaround in case sequence is empty  I shouldn't need this but ojb seems to work weird with this case
        if(note!=null&&note.getNoteIdentifier()==null&&note.getAttachment()!=null) {
            Attachment attachment = note.getAttachment();
            note.setAttachment(null);
            //store without attachment
            getPersistenceBrokerTemplate().store(note);
            attachment.setNoteIdentifier(note.getNoteIdentifier());
            //put attachment back
            note.setAttachment(attachment);
        }
        getPersistenceBrokerTemplate().store(note);
    }

    /**
     * Deletes a note from the DB using OJB.
     */
    public void deleteNote(Note note) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(note);
    }

    /**
     * Retrieves document associated with a given object using OJB.
     *
     * @param id
     * @return
     */
    public ArrayList findByremoteObjectId(String remoteObjectId) {
        Criteria criteria = new Criteria();
        //TODO: Notes - Chris move remoteObjectId string to constants
        criteria.addEqualTo("RMT_OBJ_ID", remoteObjectId);

        QueryByCriteria query = QueryFactory.newQuery(Note.class, criteria);
        //while this is currently called every time these methods could be changed to allow
        //custom sorting by BO see discussion on Notes confluence page
        defaultOrderBy(query);
        Collection notes = findCollection(query);

        return new ArrayList(notes);
    }

    /**
     * This method defines the default sort for notes
     * @param query
     */
    private void defaultOrderBy(QueryByCriteria query) {
        //TODO: Notes - Chris move remoteObjectId string to constants
        query.addOrderBy("notePostedTimestamp", true);
    }


    /**
     * Retrieve a Collection of note instances found by a query.
     *
     * @param query
     * @return
     */
    private Collection findCollection(Query query) throws DataAccessException {
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
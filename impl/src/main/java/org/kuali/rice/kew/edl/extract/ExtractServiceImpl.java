package org.kuali.rice.kew.edl.extract;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.edl.extract.dao.ExtractDAO;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.notes.dao.NoteDAO;
import org.kuali.rice.kew.routetemplate.RuleServiceImpl;
import org.kuali.rice.kew.routetemplate.dao.RuleDAO;


public class ExtractServiceImpl implements ExtractService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractServiceImpl.class);

	private ExtractDAO extractDAO;

	public Dump getDumpByDocumentId(Long noteId) {
		return getExtractDAO().getDumpByRouteHeaderId(noteId);
	}

	public void saveDump(Dump dump) {
		try {
			getExtractDAO().saveDump(dump);
			if (! dump.getFields().isEmpty()){
				for (Iterator iter = dump.getFields().iterator(); iter.hasNext();) {
					Fields field = (Fields) iter.next();
					getExtractDAO().saveField(field);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setExtractDAO(ExtractDAO extractDAO) {
		this.extractDAO = extractDAO;
	}


	public void deleteDump(Long docId) {
		try {
			getExtractDAO().deleteDump(docId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ExtractDAO getExtractDAO() {
		return extractDAO;
	}
}

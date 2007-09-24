package edu.iu.uis.eden.edl.extract;

import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.edl.extract.dao.ExtractDAO;
import edu.iu.uis.eden.notes.Attachment;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.notes.dao.NoteDAO;
import edu.iu.uis.eden.routetemplate.RuleServiceImpl;
import edu.iu.uis.eden.routetemplate.dao.RuleDAO;

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

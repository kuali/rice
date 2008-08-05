package org.kuali.rice.kew.edl.extract;


public interface ExtractService {
	
    public void saveDump(Dump dump);
    public void deleteDump(Long docId);
	public Dump getDumpByDocumentId(Long noteId);

}

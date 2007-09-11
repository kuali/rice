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
package edu.iu.uis.eden.notes;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.NoteVO;

public class NoteWebServiceTest extends KEWTestCase {
	
	@Test public void testNotesClient() throws Exception {
		NoteVO testNoteVO;
		WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
		//Test add notes
		testNoteVO = new NoteVO();
		testNoteVO.setNoteAuthorWorkflowId("andlee");
		testNoteVO.setNoteText("first added note");
		doc.updateNote(testNoteVO);
		
		testNoteVO = new NoteVO();
		testNoteVO.setNoteAuthorWorkflowId("rou");
		testNoteVO.setNoteText("second added note");
		doc.updateNote(testNoteVO);
		
		List notesList = doc.getNoteList();
		
        assertEquals ("Two notes are added.", 2, notesList.size());
       /* int i = 0;
        for (Iterator it= notesList.iterator(); it.hasNext();){
        	NoteVO displayNoteVO = (NoteVO)it.next();
        	System.out.println("i=" + i);
        	System.out.println(displayNoteVO.getNoteAuthorWorkflowId());
        	System.out.println(displayNoteVO.getNoteText());
        	if (i ==0){
        		assertEquals("The first note Text is equals 'first added note", "first added note", displayNoteVO.getNoteText());
        	}
        	i++;
        }*/
        
        doc.saveRoutingData();
        
        int i = 0;
        notesList = doc.getNoteList();
        assertEquals("Note List size changed",2,notesList.size());
        for (Iterator iter = notesList.iterator(); iter.hasNext();) {
			NoteVO noteVO = (NoteVO) iter.next();
			assertNotNull("Note saved through workflow document", noteVO.getNoteId());
			System.out.println("Note ID is:" + noteVO.getNoteId());
			i++;
			if (i ==1) {
				assertEquals("text altered during save", "first added note", noteVO.getNoteText());
				assertEquals("note user associated with saved note", "andlee", noteVO.getNoteAuthorWorkflowId());
			}
			if (i ==2) {
				assertEquals("text altered during save", "second added note", noteVO.getNoteText());
				assertEquals("note user associated with saved note", "rou", noteVO.getNoteAuthorWorkflowId());
			}
			
		}
        
        /*List notesFromDB = SpringServiceLocator.getNoteService().getNotesByRouteHeaderId(doc.getRouteHeaderId());
        for (Iterator iter = notesFromDB.iterator(); iter.hasNext();) {
			Note note = (Note) iter.next();
			System.out.println(note.getNoteText());
		}*/
        
        notesList = doc.getNoteList();
        testNoteVO = (NoteVO)notesList.get(0);
        doc.deleteNote(testNoteVO);
        
        testNoteVO = (NoteVO)notesList.get(1);
        testNoteVO.setNoteText("Update second note text");
        doc.updateNote(testNoteVO);
        
        doc.saveRoutingData();
        i = 0;
        notesList = doc.getNoteList();
        assertEquals("Note List size changed",1,notesList.size());
        for (Iterator iter = notesList.iterator(); iter.hasNext();) {
			NoteVO noteVO = (NoteVO) iter.next();
			assertNotNull("Note saved through workflow document", noteVO.getNoteId());
			System.out.println("Note ID is:" + noteVO.getNoteId());
			i++;
			if (i ==1) {
				assertEquals("text altered during save", "Update second note text", noteVO.getNoteText());
				assertEquals("note user associated with saved note", "rou", noteVO.getNoteAuthorWorkflowId());
			}
		}
		
	}
}

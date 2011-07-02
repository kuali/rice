/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.helpentry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.kew.help.dao.HelpDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;


/**
 * Tests DB persistence using JPA and OJB. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
//@KEWTransactionalTest
@Ignore("KULRICE-2090")
public class HelpEntryJpaOjbTest extends KEWTestCase {
	
	private static String HELPKEY = "testhepentry1";
	private static String HELPKEY2 = "testhelpentry2";
	private static String HELPKEY3 = "diffhepentry4";
	private static String NEW_HELPKEY = "brandNewhelpentry5";
	
	
	private static final String NON_EXISTENT_KEY = "BogusKey";
	private static final String NAME_PATTERN1 = "test";
	private static final String NAME_PATTERN2 = "test help";
	private static final String NAME_PATTERN3 = "TEST";
	private static final String NAME_PATTERN4 = "test Help";
	private static final String NAME_PATTERN5 = "diff";
	private static final String NAME_PATTERN6 = "test diff";
	private static final String NAME_PATTERN7 = "help";
	
	private static final String KEY_PATTERN1 = "entry";
	private static final String KEY_PATTERN2 = "2";
	private static final String KEY_PATTERN3 = "hep";
	private static final String KEY_PATTERN4 = "ff";
	private static final String KEY_PATTERN5 = "testhepentry";
	private static final String KEY_PATTERN6 = "esthepentry";
	private static final String KEY_PATTERN7 = "testhepentry3";
	
	private static final String TEXT_PATTERN1 = "test";
	private static final String TEXT_PATTERN2 = "help";
	private static final String TEXT_PATTERN3 = "<";
	private static final String TEXT_PATTERN4 = "<p>";
	private static final String TEXT_PATTERN5 = "entry";
	private static final String TEXT_PATTERN6 = "hep";
	private static final String TEXT_PATTERN7 = "</p>><a href";
	
	private static final String COMBO_PATTERN1 = "test,entry1,entry";
	private static final String COMBO_PATTERN2 = "help,entry,help";
	private static final String COMBO_PATTERN3 = "help,entry,<p>";
	
 	private String nsArray[] = {NAME_PATTERN1, NAME_PATTERN2, NAME_PATTERN3, NAME_PATTERN4, NAME_PATTERN5, NAME_PATTERN6, NAME_PATTERN7};
 	private String ksArray[] = {KEY_PATTERN1, KEY_PATTERN2, KEY_PATTERN3, KEY_PATTERN4, KEY_PATTERN5, KEY_PATTERN6, KEY_PATTERN7};
 	private String tsArray[] = {TEXT_PATTERN1, TEXT_PATTERN2, TEXT_PATTERN3, TEXT_PATTERN4, TEXT_PATTERN5, TEXT_PATTERN6, TEXT_PATTERN7};
 	private String csArray[] = {COMBO_PATTERN1, COMBO_PATTERN2, COMBO_PATTERN3};
	private Long idForJpa;
	private Long idForOjb;
	private static Long NON_EXISTENT_ID = 999999999l;

	private HelpDAO jpaDao;
	private HelpDAO ojbDao;
	
	protected void loadTestData() throws Exception {
    	loadXmlFile("HelpEntryConfig.xml");
    }
		
	@Override
	protected void setUpInternal() throws Exception {
		super.setUpInternal();
		this.jpaDao = (HelpDAO) KEWServiceLocator.getBean("enHelpDAO");
		this.ojbDao = (HelpDAO) KEWServiceLocator.getBean("enHelpOJBDAO");

	}
	
	@Test
	public void testFindByKey() {
		// ensure both results are identical
		HelpEntry helpJdo = ojbDao.findByKey(HELPKEY);
		HelpEntry helpJpa = jpaDao.findByKey(HELPKEY);
		if (helpJdo != null && helpJpa != null) {
			assertTrue("Help names are different between ojb + jpa findByKey ", helpJdo.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("Help keys  are different between ojb + jpa findByKey ", helpJdo.getHelpKey().equals(helpJpa.getHelpKey()));
		    assertTrue("Help text  are different between ojb + jpa findByKey ", helpJdo.getHelpText().equals(helpJpa.getHelpText()));
		    assertTrue("Help id    are different between ojb + jpa findByKey ", helpJdo.getHelpId().equals(helpJpa.getHelpId()));
		    assertTrue("Help db_lock are different between ojb + jpa findByKey ", helpJdo.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		    System.out.println("Name " + helpJpa.getHelpName() + " Key " + helpJpa.getHelpKey() + " Text " + helpJpa.getHelpText() + " ID " + helpJpa.getHelpId() + " Lock " + helpJpa.getLockVerNbr());	
		    System.out.println("Name " + helpJdo.getHelpName() + " Key " + helpJdo.getHelpKey() + " Text " + helpJdo.getHelpText() + " ID " + helpJdo.getHelpId() + " Lock " + helpJdo.getLockVerNbr());	
		}    
		// ensure both can handle a not found condition by returning null
		assertNull("Expected null value not returned by a non-existing findByKey ", ojbDao.findByKey(NON_EXISTENT_KEY));
		assertNull("Expected null value not returned by a non-existing findByKey ", jpaDao.findByKey(NON_EXISTENT_KEY));
		System.out.println("Test testFindByKey completed.");
	}
	
	@Test
	public void testFindById() {
		// load keys to use.
		loadHelpID();
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpId(idForJpa);
		HelpEntry ojbHE = new HelpEntry();
		ojbHE.setHelpId(idForOjb);
		
		// access db with ojbect that we created that should now have a correct id key loaded. Expect object to be found.
		HelpEntry jpa2HE = jpaDao.findById(jpaHE.getHelpId());
		HelpEntry ojb2HE = ojbDao.findById(ojbHE.getHelpId());
		if (jpa2HE != null && ojb2HE != null) {
			assertTrue("Help names are different between ojb + jpa findById ", ojb2HE.getHelpName().equals(jpa2HE.getHelpName()));
			assertTrue("Help keys  are different between ojb + jpa findById ", ojb2HE.getHelpKey().equals(jpa2HE.getHelpKey()));
		    assertTrue("Help text  are different between ojb + jpa findById ", ojb2HE.getHelpText().equals(jpa2HE.getHelpText()));
		    assertTrue("Help id    are different between ojb + jpa findById ", ojb2HE.getHelpId().equals(jpa2HE.getHelpId()));
		    assertTrue("Help db_lock are different between ojb + jpa findById ", ojb2HE.getLockVerNbr().equals(jpa2HE.getLockVerNbr()));
		    System.out.println("Name= " + jpa2HE.getHelpName() + " Key= " + jpa2HE.getHelpKey() + " Text= " + jpa2HE.getHelpText() + " ID= " + jpa2HE.getHelpId() + " Lock= " + jpa2HE.getLockVerNbr());	
		    System.out.println("Name= " + ojb2HE.getHelpName() + " Key= " + ojb2HE.getHelpKey() + " Text= " + ojb2HE.getHelpText() + " ID= " + ojb2HE.getHelpId() + " Lock= " + ojb2HE.getLockVerNbr());	
		}  else {
			// then both should be null
			assertTrue("Expected null values for both objects ", (ojb2HE == null && jpa2HE == null));
			System.out.println("Unexpected null objects returned from findById ");
			}

		// ensure both can handle a not found condition by returning null
		assertNull("Expected null value not returned by a non-existing findById ", ojbDao.findById(NON_EXISTENT_ID));
		assertNull("Expected null value not returned by a non-existing findById ", jpaDao.findById(NON_EXISTENT_ID));
		System.out.println("Test testFindById completed.");
	}
	
	@Test
	public void testSearch1() {
		// load keys to use.
		System.out.println("Beginning testSearch");
		loadHelpID();
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpId(idForJpa);
		HelpEntry ojbHE = new HelpEntry();
		ojbHE.setHelpId(idForOjb);
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa ", ojbDao.search(ojbHE).size(), jpaDao.search(jpaHE).size());
		System.out.println("size of lists returned are equal and = " + ojbDao.search(jpaHE).size());
		
		// ensure both results sets contain same contents (cannot use containsAll because object does not implement ".equals" method therefore
		// the containsAll defaults to an "==" test which fails because objects have different addresses.)
		
		// uncomment showListContents to see difference.
		List<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
		//System.out.println("following list is from OJB");
		//showListContents(colOjbHE);
		List<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
		//System.out.println("following list is from OJB");
		//showListContents(colJpaHE);
		
		
		// match fields in OJB collection with fields from Jpa collection.
		int index = 0;
		for (HelpEntry helpOjb : colOjbHE) {
			// return obj from jpa collection at index i.
			HelpEntry helpJpa = colJpaHE.get(index);
			index++;
			assertTrue("A HelpId   difference was detected in testSearch ",helpOjb.getHelpId().equals(helpJpa.getHelpId()));
			assertTrue("A HelpKey  difference was detected in testSearch ",helpOjb.getHelpKey().equals(helpJpa.getHelpKey()));
			assertTrue("A HelpName difference was detected in testSearch ",helpOjb.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("A HelpText difference was detected in testSearch ",helpOjb.getHelpText().equals(helpJpa.getHelpText()));
			assertTrue("A HelpLock difference was detected in testSearch ",helpOjb.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		}
		
		System.out.println("results contain same data. ");
		
		

		

//		Collection<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
//		Collection<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
//		for (HelpEntry ojbHE2 : colOjbHE) {
//			private HelpEntry getJpaFromList(colJpaHe);
//		}
//		HelpEntry helpJdo = ojbDao.search(hOjb);
//		HelpEntry helpJpa = jpaDao.search(HELPID);
//		if (helpJdo != null && helpJpa != null) {
//			assertTrue("Help names are different between ojb + jpa findSearch ", helpJdo.getHelpName().equals(helpJpa.getHelpName()));
//			assertTrue("Help keys  are different between ojb + jpa findSearch ", helpJdo.getHelpKey().equals(helpJpa.getHelpKey()));
//		    assertTrue("Help text  are different between ojb + jpa findSearch ", helpJdo.getHelpText().equals(helpJpa.getHelpText()));
//		    assertTrue("Help id    are different between ojb + jpa findSearch ", helpJdo.getHelpId().equals(helpJpa.getHelpId()));
//		    assertTrue("Help db_lock are different between ojb + jpa findSearch ", helpJdo.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
//		    System.out.println("Name " + helpJpa.getHelpName() + " Key " + helpJpa.getHelpKey() + " Text " + helpJpa.getHelpText() + " ID " + helpJpa.getHelpId() + " Lock " + helpJpa.getLockVerNbr());	
//		    System.out.println("Name " + helpJdo.getHelpName() + " Key " + helpJdo.getHelpKey() + " Text " + helpJdo.getHelpText() + " ID " + helpJdo.getHelpId() + " Lock " + helpJdo.getLockVerNbr());	
//		}  else {
//			// then both should be null
//			assertTrue("Expected null values for both objects ", (helpJdo == null && helpJpa == null));
//			}
//
//		// ensure both can handle a not found condition by returning null
//		assertNull("Expected null value not returned by a non-existing findSearch ", ojbDao.search(helpEntry)(NON_EXISTENT_ID));
//		assertNull("Expected null value not returned by a non-existing findSearch ", jpaDao.search(NON_EXISTENT_ID));
//		System.out.println("Test testFindSearch completed.");
	}
	
	@Test
	public void testSearch2() {	
		for (String pattern : nsArray) {
			nameChecks(pattern);
		}	
		// to individually test a name search pattern so data comes fresh into persistence context see below strategy. (found same results whether looping or individual)
		// 1) comment out loop above and active below code.
		// nameChecks(NAME_PATTERN7); 
	}
	
	private void nameChecks(String namePattern) {
		// test pattern usage 
		HelpEntry ojbHE = new HelpEntry();
		ojbHE.setHelpName(namePattern); 
		List<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpName(ojbHE.getHelpName());
		List<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa ", colOjbHE.size(), colJpaHE.size());
		System.out.println("Search2 size of lists returned are equal and = " + colJpaHE.size());
		// match fields in OJB collection with fields from Jpa collection.
		int index = 0;
		for (HelpEntry helpOjb : colOjbHE) {
			// return obj from jpa collection at index i.
			HelpEntry helpJpa = colJpaHE.get(index);
			index++;
			assertTrue("A HelpId   difference was detected in testSearch2 ",helpOjb.getHelpId().equals(helpJpa.getHelpId()));
			assertTrue("A HelpKey  difference was detected in testSearch2 ",helpOjb.getHelpKey().equals(helpJpa.getHelpKey()));
			assertTrue("A HelpName difference was detected in testSearch2 ",helpOjb.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("A HelpText difference was detected in testSearch2 ",helpOjb.getHelpText().equals(helpJpa.getHelpText()));
			assertTrue("A HelpLock difference was detected in testSearch2 ",helpOjb.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		}
		System.out.println("Name pattern results contain same data. ");
	}
	
	@Test
	public void testSearch3() {	
		for (String pattern : ksArray) {
			keyChecks(pattern);
		}	
		// to individually test a name search pattern so data comes fresh into persistence context see below strategy. (found same results whether looping or individual)
		// 1) comment out loop above and active below code.
		// nameChecks(NAME_PATTERN7); 
	}
	
	private void keyChecks(String keyPattern) {
		// test pattern usage 
		HelpEntry ojbHE = new HelpEntry();
		ojbHE.setHelpKey(keyPattern); 
		List<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpKey(ojbHE.getHelpKey());
		List<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa ", colOjbHE.size(), colJpaHE.size());
		System.out.println("Search3 size of lists returned are equal and = " + colJpaHE.size());
		// match fields in OJB collection with fields from Jpa collection.
		int index = 0;
		for (HelpEntry helpOjb : colOjbHE) {
			// return obj from jpa collection at index i.
			HelpEntry helpJpa = colJpaHE.get(index);
			index++;
			assertTrue("A HelpId   difference was detected in testSearch3 ",helpOjb.getHelpId().equals(helpJpa.getHelpId()));
			assertTrue("A HelpKey  difference was detected in testSearch3 ",helpOjb.getHelpKey().equals(helpJpa.getHelpKey()));
			assertTrue("A HelpName difference was detected in testSearch3 ",helpOjb.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("A HelpText difference was detected in testSearch3 ",helpOjb.getHelpText().equals(helpJpa.getHelpText()));
			assertTrue("A HelpLock difference was detected in testSearch3 ",helpOjb.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		}
		System.out.println("Key pattern results contain same data. ");
	}
	
	@Test
	public void testSearch4() {	
		for (String pattern : tsArray) {
			textChecks(pattern);
		}	
	}
	
	private void textChecks(String pattern) {
		// test pattern usage 
		HelpEntry ojbHE = new HelpEntry();
		ojbHE.setHelpText(pattern); 
		List<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpText(ojbHE.getHelpText());
		List<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa ", colOjbHE.size(), colJpaHE.size());
		System.out.println("Search4 size of lists returned are equal and = " + colJpaHE.size());
		// match fields in OJB collection with fields from Jpa collection.
		int index = 0;
		for (HelpEntry helpOjb : colOjbHE) {
			// return obj from jpa collection at index i.
			HelpEntry helpJpa = colJpaHE.get(index);
			index++;
			assertTrue("A HelpId   difference was detected in testSearch4 ",helpOjb.getHelpId().equals(helpJpa.getHelpId()));
			assertTrue("A HelpKey  difference was detected in testSearch4 ",helpOjb.getHelpKey().equals(helpJpa.getHelpKey()));
			assertTrue("A HelpName difference was detected in testSearch4 ",helpOjb.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("A HelpText difference was detected in testSearch4 ",helpOjb.getHelpText().equals(helpJpa.getHelpText()));
			assertTrue("A HelpLock difference was detected in testSearch4 ",helpOjb.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		}
		System.out.println("Text pattern results contain same data. ");
	}
	
	@Test
	public void testSearch5() {	
		for (String pattern : csArray) {
			comboChecks(pattern);
		}	
	}
	
	private void comboChecks(String pattern) {
		// test pattern usage
		HelpEntry ojbHE = new HelpEntry();
		//parse the multiple tokens and load helpEntry fields.
		StringTokenizer parser = new StringTokenizer(pattern, ","); // parse on comma's
		// input data must have 3 tokens separated by comma's in Name, Key, Text order.
		ojbHE.setHelpName(parser.nextToken());
		ojbHE.setHelpKey(parser.nextToken());
		ojbHE.setHelpText(parser.nextToken());
		List<HelpEntry> colOjbHE = ojbDao.search(ojbHE);
		HelpEntry jpaHE = new HelpEntry();
		jpaHE.setHelpName(ojbHE.getHelpName());
		jpaHE.setHelpKey(ojbHE.getHelpKey());
		jpaHE.setHelpText(ojbHE.getHelpText());
		List<HelpEntry> colJpaHE = ojbDao.search(jpaHE);
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa ", colOjbHE.size(), colJpaHE.size());
		System.out.println("Search5 size of lists returned are equal and = " + colJpaHE.size());
		// match fields in OJB collection with fields from Jpa collection.
		int index = 0;
		for (HelpEntry helpOjb : colOjbHE) {
			// return obj from jpa collection at index i.
			HelpEntry helpJpa = colJpaHE.get(index);
			index++;
			assertTrue("A HelpId   difference was detected in testSearch5 ",helpOjb.getHelpId().equals(helpJpa.getHelpId()));
			assertTrue("A HelpKey  difference was detected in testSearch5 ",helpOjb.getHelpKey().equals(helpJpa.getHelpKey()));
			assertTrue("A HelpName difference was detected in testSearch5 ",helpOjb.getHelpName().equals(helpJpa.getHelpName()));
			assertTrue("A HelpText difference was detected in testSearch5 ",helpOjb.getHelpText().equals(helpJpa.getHelpText()));
			assertTrue("A HelpLock difference was detected in testSearch5 ",helpOjb.getLockVerNbr().equals(helpJpa.getLockVerNbr()));
		}
		System.out.println("Combo pattern results contain same data. ");
	}
	
	// Test update and insert functionality.
	@Test
	public void testSaveJpa() throws Exception {
		testSave(jpaDao);
		System.out.println("Test testSaveJpa completed.");
	}
	
	@Test
	public void testSaveOjb() throws Exception {
		testSave(ojbDao);
		System.out.println("Test testSaveOjb completed.");
	}
	
	private void testSave(HelpDAO dao) {
		HelpEntry hE = dao.findByKey(HELPKEY);
		assertEquals("Incorrect match on findByKey in testSave ", HELPKEY, hE.getHelpKey());
		
		// update the value
		hE.setHelpName("abc");
		dao.save(hE);
		
		// validate that it was updated correctly.
		hE = dao.findByKey(HELPKEY);
		assertEquals("HelpEntry Name value was not properly updated.", "abc", hE.getHelpName());
		System.out.println("Existing HelpEntry has been updated.");
		
		// insert a new HelpEntry
		// First validate it does not exist
		hE = dao.findByKey(NEW_HELPKEY);
		if (hE != null) {
		    assertEquals("HelpEntry to be inserted already exists.", NEW_HELPKEY, dao.findByKey(NEW_HELPKEY));
		}
		// insert a value
		hE = new HelpEntry();
		hE.setHelpKey(NEW_HELPKEY);
		hE.setHelpName("test help name entry 5");
		hE.setHelpText("test help text entry 5");
		dao.save(hE);
		
		// validate that it was inserted
		hE = dao.findByKey(NEW_HELPKEY);
		System.out.println("inserted helpEntry key=" + hE.getHelpKey() + ", name=" + hE.getHelpName() + ", text=" + hE.getHelpText());
		assertEquals("HelpEntry key was not found after an insert.", NEW_HELPKEY, hE.getHelpKey());
		assertEquals("HelpEntry Name did not equal expected value after an insert.", "test help name entry 5", hE.getHelpName());
		assertEquals("HelpEntry Text did not equal expected value after an insert.", "test help text entry 5", hE.getHelpText());
	}
	
	// Test delete functionality.
	@Test
	public void testDeleteHelpEntryJpa() throws Exception {
		System.out.println("Test testDeleteHelpEntryJpa starting.");
		
		// Attempt to delete a row that does NOT exist. Differences between OJB and JPA exist. OJB throws an exception, JPA does not.
		// This difference has been noted in Confluence in KULRICE Global Technical Guides named Object-Relational Mapping Library Differences
		HelpEntry hE = new HelpEntry();
		hE.setHelpId(NON_EXISTENT_ID);
		jpaDao.deleteEntry(hE);
		System.out.println("No exception was thrown from an attempt to delete a NON-existing HelpEntry using JPA loaded primary key field.");
				
		// Delete an existing HelpEntry after finding it first.
		HelpEntry hE2 = jpaDao.findByKey(HELPKEY);
		jpaDao.deleteEntry(hE2);
		System.out.println("HelpEntry has been deleted. Key=" + HELPKEY);
		// JPA allows a deleted object that priorly came from the db, to be retrieved again - it's value is null, ( it must have already existed and been retrieved from db first.)
		assertNull("HelpEntry KEY was not properly deleted.", jpaDao.findByKey(HELPKEY));
		System.out.println("Completed a findByName, then delete, then findByKEY again, in testDeleteHelpEntryJPA successfully.");
		// access same record jpa deleted using ojb
		assertNull("OJB access of record JPA just deleted not null as expected" , ojbDao.findByKey(HELPKEY));
		
		
		// Delete an existing HelpEntry db record by creating a new object instance and loading the existing db record's primary key, then delete it.
		// Need primary key which changes with every test. Get it from an OJB find.
		
		HelpEntry ojbHE = ojbDao.findByKey(HELPKEY2);
		
		HelpEntry hE3 = new HelpEntry();
		hE3.setHelpId(ojbHE.getHelpId());
		System.out.println("JPA is about to delete HelpEntry with Name=" + ojbHE.getHelpName());
		jpaDao.deleteEntry(hE3);
		System.out.println("Deleted an existing HelpEntry db record by creating a new object instance and loading primary key.");
		// cannot attempt to access the deleted JPA record which was never retrieved from db (otherwise exception is thrown).

		// ojb can still find the record jpa deleted because it found it earlier therefore it is in it's context and is not aware that JPA deleted it.  
		HelpEntry ojbHE2 = ojbDao.findByKey(HELPKEY2);
		System.out.println("ojb re-accessed HelpEntry deleted by JPA, Name=" + ojbHE2.getHelpName());  
		//assertNull("OJB access of record JPA just deleted not null as expected" , ojbDao.findByKey(HELPKEY2));
		
		// attempt to re-access record JPA deleted. (should cause exception).
		//assertNull("HelpEntry KEY was not properly deleted.", jpaDao.findByKey(HELPKEY2));
		
		System.out.println("Test testDeleteHelpEntryJpa completed.");
	}

	
		
	
	private void showListContents(Collection<HelpEntry> coll) {
		// dump variable contents in list returned from JPA access.
		for (HelpEntry help : coll) {
			System.out.println("Name= " + help.getHelpName() + " Key= " + help.getHelpKey() + " Text= " + help.getHelpText() + " ID= " + help.getHelpId() + " Lock= " + help.getLockVerNbr());	
		}
	}
	
	private HelpEntry getObjFromCollection(List<HelpEntry> coll, int index) {
		return coll.get(index);
	}
	
 	
	private void loadHelpID() {
		// key is a sequence so it changes each test!
		// Strategy - use ojb find by name to get a key for jpa, then load jpa object with the key and perform access.
		HelpEntry helpOjb = ojbDao.findByKey(HELPKEY);
		HelpEntry helpJpa = jpaDao.findByKey(HELPKEY);
		// stop test if either or both are null
		if (helpOjb == null || helpJpa == null) {
			assertTrue("Cannot complete this test unless objects are not null ", (1==2));
		}
		idForJpa = helpOjb.getHelpId();
		idForOjb = helpJpa.getHelpId();
	}
}

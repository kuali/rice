package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.test.KNSTestCase;

/**
 *  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PatternedStringBuilderTest extends KNSTestCase{
 
 @Test
 public void testSprintf(){
  PatternedStringBuilder patterenedStringBuilder = new PatternedStringBuilder("int");
  
  patterenedStringBuilder.setPattern("int");
  String expectedVal = patterenedStringBuilder.sprintf(5);
  assertEquals("5",expectedVal);
 }
 

}
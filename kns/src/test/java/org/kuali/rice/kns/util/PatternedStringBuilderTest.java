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
  double pi = Math.PI;
  PatternedStringBuilder patterenedStringBuilder = new PatternedStringBuilder("pi = %5.3f");
  
  //patterenedStringBuilder.setPattern("int");
  String expectedVal = patterenedStringBuilder.sprintf(pi);
  assertEquals("pi = 3.142",expectedVal);
 }
 

}
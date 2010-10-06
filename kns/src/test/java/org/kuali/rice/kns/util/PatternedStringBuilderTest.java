package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.test.KNSTestCase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PatternedStringBuilderTest extends KNSTestCase {

    @Test
    public void testSprintf() {
        double pi = Math.PI;
        PatternedStringBuilder patterenedStringBuilder = new PatternedStringBuilder("pi = %5.3f");
        String expectedVal = patterenedStringBuilder.sprintf(pi);
        assertEquals("pi = 3.142", expectedVal);
        patterenedStringBuilder.setPattern("%4$2s %3$2s %2$2s %1$2s");
        assertEquals(" z  y  x  w", patterenedStringBuilder.sprintf("w", "x", "y", "z"));
        patterenedStringBuilder.setPattern("");
        assertEquals("", patterenedStringBuilder.sprintf("somethingElse"));
        // This basically replicates tests done via jdk to test java.util.Formatter.java
    }
}
package org.kuali.rice.core.api.util;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * Test cases for VersionHelper class
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

public class VersionHelperTest extends TestCase
{

    @Test
    public void testVersionHelper() {
        String verOne[] = {"1.2.3","6.8.83.4","5.0","snapshot-2.3.5", "something-0.333.447...-nice" };
        String verTwo[] = {"1.2.1","6.8.83","5.9","2.3-snapshot", "0.345...777" };
        boolean results[] = {false, false, true, false, true, false};


        for(int i=0;i<verOne.length;i++) {
            try {
                assertEquals(VersionHelper.compareVersions(verOne[i], verTwo[i]),results[i]);
                assertEquals(VersionHelper.compareVersions(verTwo[i], verOne[i]),!results[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //lets check the case where the version numbers are equal

        try{
            assertEquals(VersionHelper.compareVersions("7.7.7", "7.7.7-SNAPSHOT"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

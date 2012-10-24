package org.kuali.rice.core.api.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Helper class for comparing version strings
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class VersionHelper {


    /**
     * returns true if versionOne is less than versionTwo, false otherwise
     *
     * @param versionOne string representation of a version
     * @param versionTwo string representation of a version
     * @return boolean
     */
    public static boolean compareVersions(String versionOne, String versionTwo) throws Exception {

        //returns true if versionOne is less than versionTwo, false otherwise

        // This pattern matches any non-digit string containing a version number of the form A.B.C.D followed by any non-digit string
        Pattern extractVersion = Pattern.compile("(\\D*)((\\d*\\.?)+)(\\D*)");

        // Pattern used for spliting version number for comparison
        Pattern defaultPattern = Pattern.compile("\\.");

        Matcher m1 = extractVersion.matcher(versionOne);
        Matcher m2 = extractVersion.matcher(versionTwo);

        if (!m1.matches() || !m2.matches()) {
            throw new Exception("Unable to extract version number from string using regex");
        }

        String sanitizedVOne = versionOne.substring(m1.start(2), m1.end(2));
        String sanitizedVTwo = versionTwo.substring(m2.start(2), m2.end(2));

        String[] oneDigits = defaultPattern.split(sanitizedVOne);
        String[] twoDigits = defaultPattern.split(sanitizedVTwo);


        boolean lessThan=true;
        int length=0;
        if (oneDigits.length<twoDigits.length) {
            length=oneDigits.length;
        } else {
            length=twoDigits.length;
        }

        for(int i=0; i<length; i++) {
            Integer intOne = Integer.valueOf(oneDigits[i]);
            Integer intTwo = Integer.valueOf(twoDigits[i]);
            Integer compare = intOne.compareTo(intTwo);
            if (compare < 0) {
                return true;
            }
            else if (compare > 0) {
                return false;
            }
        }
        if (twoDigits.length>oneDigits.length) {
            return true;
        }
        return false;

    }

}



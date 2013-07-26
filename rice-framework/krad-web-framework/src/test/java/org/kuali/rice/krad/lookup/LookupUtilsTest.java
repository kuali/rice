package org.kuali.rice.krad.lookup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.search.SearchOperator;

import java.util.Map;
import java.util.HashMap;

/**
 * Unit test for LookupUtils.
 *
 * @author Rice Team (rice.collab@kuali.org)
 */
public class LookupUtilsTest {

    @Test
    public void testScrubQueryCharacters() {
        // build up some sample values to scrub
        Map<String, String> queryCharacterSamples = new HashMap<String, String>();
        queryCharacterSamples.put(null, null);
        queryCharacterSamples.put("", "");
        queryCharacterSamples.put("this is a string with no query characters", "this is a string with no query characters");
        queryCharacterSamples.put("this is a string with one.. query character", "this is a string with one query character");
        queryCharacterSamples.put("..test...test", "testtest");
        StringBuilder allQueryCharacters = new StringBuilder();
        for (SearchOperator operator : SearchOperator.QUERY_CHARACTERS) {
            allQueryCharacters.append(operator.op());
        }
        queryCharacterSamples.put(allQueryCharacters.toString(), "");

        // scrub them and make sure they produce the proper output
        for (String input : queryCharacterSamples.keySet()) {
            String output = queryCharacterSamples.get(input);
            Assert.assertEquals("Check failed for input: " + input, output, LookupUtils.scrubQueryCharacters(input));
        }
    }



}

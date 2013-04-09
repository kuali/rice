package edu.samplu.travel.krad.test;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: dseibert
 * Date: 4/9/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class UIFComponentValidationRegexPatternSTJUnitBkMrkIT extends UIFComponentValidationRegexPatternSTJUnitBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }
    @Test
    public void testValidCharacterConstraintBookmark() throws Exception {
        testValidCharacterConstraintBookmark(this);
    }
}


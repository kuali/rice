package edu.samplu.travel.krad.test;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: dseibert
 * Date: 4/9/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIFComponentValidationRegexPatternSTNGNavGen extends UIFComponentValidationRegexPatternSTNGBase {
    @Test(groups = { "all", "fast", "default", "bookmark" }, description = "testValidCharacterConstraintNav")
    @Parameters( { "seleniumHost", "seleniumPort", "os", "browser", "version", "webSite" })
    public void testValidCharacterConstraintNav() throws Exception {
        setUp();
        testValidCharacterConstraintNav(this);
    }

}

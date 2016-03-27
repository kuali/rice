package org.kuali.rice.kim.impl.data;

import java.util.List;

/**
 * A simple service for checking integrity of KIM data and for performing automatic repair of the data if issues are
 * found.
 *
 * @author Eric Westfall
 */
public interface DataIntegrityService {

    /**
     * Performs and integrity check on KIM data, returning a list of messages.
     *
     * @return a list of messages detailing the results of the integrity check
     */
    List<String> checkIntegrity();

    /**
     * Executes any automatic repair of data integrity issues on KIM data, returning a list of messages.
     *
     * @return a list of messages detailing the results of the data repair
     */
    List<String> repair();

}

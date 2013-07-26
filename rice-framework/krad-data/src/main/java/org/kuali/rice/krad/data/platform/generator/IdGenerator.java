package org.kuali.rice.krad.data.platform.generator;

import java.sql.Connection;

/**
 * Thin interface for primary key id generators abstraction.
 * An IdGenerator is not a singleton, it is a concrete instance
 * initialized in an implementation-specific manner to represent
 * a concrete backend source of specific id.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface IdGenerator {

    /**
     * Each IdGenerator must have a name which is at least as unique
     * as the backend implementation
     *
     * @return the name of this generator
     */
    String getName();

    /**
     * Generate the next id value given a {@link Connection}
     *
     * @param connection the jdbc Connection
     *
     * @return the next unique id value for this generator
     */
    public Object getNextValue(Connection connection);
}

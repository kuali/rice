/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.platform;

import org.apache.commons.lang.StringUtils;

/**
 * Contains database platform information, specifically the name and major version of the database. The name of the
 * database platform should be considered as case insensitive.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DatabasePlatformInfo {

    private final String name;
    private final int majorVersion;

    public DatabasePlatformInfo(String name, int majorVersion) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
        this.majorVersion = majorVersion;
    }

    public String getName() {
        return name;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DatabasePlatformInfo)) {
            return false;
        }

        final DatabasePlatformInfo that = (DatabasePlatformInfo) object;

        if (!name.equals(that.name)) {
            return false;
        }
        if (majorVersion != that.majorVersion) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + majorVersion;
        return result;
    }

    @Override
    public String toString() {
        return "DatabasePlatformInfo{" +
                "name='" + name + '\'' +
                ", majorVersion=" + majorVersion +
                '}';
    }
}

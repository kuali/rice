/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.impex.xml;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * A "singleton" XmlDocCollection backed by a single location (LocationXmlDoc)
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LocationXmlDocCollection extends BaseXmlDocCollection {

    public LocationXmlDocCollection(String location) {
        this(location, LocationXmlDoc.getName(location));
    }

    public LocationXmlDocCollection(String location, String name) {
        super(new File(name)); // This doesn't represent a real file, but the ingester service doesn't seem to care
        Preconditions.checkArgument(!StringUtils.isBlank(location), "'location' cannot be blank");
        Preconditions.checkArgument(!StringUtils.isBlank(name), "'name' cannot be blank");
        xmlDocs.add(new LocationXmlDoc(location, name, this));
    }

}

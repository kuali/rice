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
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class BidirectionalOwnerRegistry {

    private final Map<String, Collection<String>> manyToManyOwnerAssignment = new HashMap<String, Collection<String>>();
    private final Map<String, Collection<String>> manyToOneOwnerAssignment = new HashMap<String, Collection<String>>();

    private static class BidirectionalOwnerRegistryHolder {
        static final BidirectionalOwnerRegistry INSTANCE = new BidirectionalOwnerRegistry();
    }

    private BidirectionalOwnerRegistry() {
        //for singleton
    }

    public static BidirectionalOwnerRegistry getInstance() {
        return BidirectionalOwnerRegistryHolder.INSTANCE;
    }

    public boolean isOwnerThisClassManyToMany(String thisClass, String itemClass) {
        final Collection<String> ownedByThisClass = manyToManyOwnerAssignment.get(thisClass);
        return ownedByThisClass != null && ownedByThisClass.contains(itemClass);
    }

    public boolean isOwnerItemClassManyToMany(String thisClass, String itemClass) {
        final Collection<String> ownedByItemClass = manyToManyOwnerAssignment.get(itemClass);
        return ownedByItemClass != null && ownedByItemClass.contains(thisClass);
    }

    public void assignThisClassAsOwnerManyToMany(String thisClass, String itemClass) {
        Collection<String> ownedByThisClass = manyToManyOwnerAssignment.get(thisClass);
        if (ownedByThisClass == null) {
            ownedByThisClass = new ArrayList<String>();
        }

        ownedByThisClass.add(itemClass);
        manyToManyOwnerAssignment.put(thisClass, ownedByThisClass);
    }

    public boolean isOwnerThisClassManyToOne(String thisClass, String itemClass) {
        final Collection<String> ownedByThisClass = manyToOneOwnerAssignment.get(thisClass);
        return ownedByThisClass != null && ownedByThisClass.contains(itemClass);
    }

    public boolean isOwnerItemClassManyToOne(String thisClass, String itemClass) {
        final Collection<String> ownedByItemClass = manyToOneOwnerAssignment.get(itemClass);
        return ownedByItemClass != null && ownedByItemClass.contains(thisClass);
    }

    public void assignItemClassAsOwnerManyToOne(String thisClass, String itemClass) {
        Collection<String> ownedByItemClass = manyToOneOwnerAssignment.get(itemClass);
        if (ownedByItemClass == null) {
            ownedByItemClass = new ArrayList<String>();
        }

        ownedByItemClass.add(thisClass);
        manyToOneOwnerAssignment.put(itemClass, ownedByItemClass);
    }
}

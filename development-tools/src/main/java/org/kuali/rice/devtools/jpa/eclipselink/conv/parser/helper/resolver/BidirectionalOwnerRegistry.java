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

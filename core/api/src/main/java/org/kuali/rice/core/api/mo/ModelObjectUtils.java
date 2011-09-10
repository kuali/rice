package org.kuali.rice.core.api.mo;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A set of simple utilities to assist with common idioms in immutable model objects and their builders.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModelObjectUtils {

    /**
     * Takes the given list of {@code ModelBuilder} objects and invokes the
     * {@link org.kuali.rice.core.api.mo.ModelBuilder#build()} method on each of them, adding them to a new list and
     * return an unmodifiable copy.  If the given list is empty or null, will return an empty and unmodifiable list.
     *
     * @param builderList the list of builders to build and add to resulting list, may be empty or null
     * @param <T> the type of the object that is built by the builders in the list, it is up to the caller of this
     *        method to ensure they define the proper parameterized list for the return type.
     * @return an unmodifiable list containing objects built from the given list of model builders
     */
    public static <T> List<T> buildImmutableCopy(List<? extends ModelBuilder> builderList) {
        if (CollectionUtils.isEmpty(builderList)) {
            return Collections.emptyList();
        }
        List<T> copy = new ArrayList<T>();
        for (ModelBuilder builder : builderList) {
            // since ModelBuilder is not parameterized, this code must assume that the appropriate type of object is built
            @SuppressWarnings("unchecked")
            T built = (T)builder.build();
            copy.add(built);
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Takes the given list and returns an unmodifiable copy of that list containing the same elements as the original
     * list.  This method handles a null list being passed to it by returning an unmodifiable empty list.
     *
     * @param listToCopy the list to copy
     * @param <T> the type of the elements in the given list
     *
     * @return an unmodifiable copy containing the same elements as the given list
     */
    public static <T> List<T> createImmutableCopy(List<T> listToCopy) {
        if (CollectionUtils.isEmpty(listToCopy)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<T>(listToCopy));
    }

    private ModelObjectUtils() {
        throw new UnsupportedOperationException("Do not call.");
    }

}

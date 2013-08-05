package org.kuali.rice.krad.uif.util;

/**
 * Represents a property reference in a path expression, for use in implementing
 * {@link ObjectPropertyUtils.PathEntry}.
 * 
 * <p>
 * This class defers the actual resolution of property references nodes in a path expression until
 * the transition between parse nodes. This facilitates traversal to the final node in the path.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @version 2.4
 * @see ObjectPropertyUtils#parsePathExpression(Object, String,
 *      org.kuali.rice.krad.uif.util.ObjectPropertyUtils.PathEntry)
 */
public class ObjectPropertyReference<T> {

    /**
     * Internal path entry implementation.
     */
    private static class ReferencePathEntry<T> implements
            ObjectPropertyUtils.PathEntry<ObjectPropertyReference<T>, ObjectPropertyReference<T>> {

        /**
         * The bean at the root of the parse path.
         */
        private final Object rootBean;

        /**
         * The logic for resolving each node in the path.
         */
        private final Resolver<T> resolver;

        /**
         * Create a new path entry for resolving parse nodes on a given bean.
         * @param rootBean The bean at the root of the parse path.
         * @param resolver The logic used to resolve each path node.
         */
        private ReferencePathEntry(Object rootBean, Resolver<T> resolver) {
            this.rootBean = rootBean;
            this.resolver = resolver;
        }

        @Override
        public ObjectPropertyReference<T> parse(
                ObjectPropertyReference<T> node, String next,
                boolean inherit) {
            Object current;
            if (node == null) {
                current = rootBean;
            } else {
                current = node.dereference();
            }
            return new ObjectPropertyReference<T>(current, next, resolver);
        }

        @Override
        public ObjectPropertyReference<T> prepare(
                ObjectPropertyReference<T> prev) {
            return prev;
        }

        @Override
        public String dereference(ObjectPropertyReference<T> prev) {
            return (String) prev.dereference();
        }
    }

    /**
     * Simple resolver interface for use with {@link #resolvePath(Object,String,Resolver},
     * representing the resolution of a single node in the path expression.
     */
    public static interface Resolver<T> {
        T dereference(Object bean, String name);
    }

    /**
     * Resolve a path expression on a bean.
     * 
     * @param bean The bean.
     * @param propertyPath The property path expression.
     * @param resolver Logical resolver implementation.
     * @return A reference to the final parse node involved in parsing the path expression.
     */
    public static <T> ObjectPropertyReference<T> resolvePath(Object bean, String propertyPath,
            Resolver<T> resolver) {
        return ObjectPropertyUtils.parsePathExpression(
                null,
                propertyPath,
                new ReferencePathEntry<T>(bean, resolver)
                );
    }

    private final Resolver<T> resolver;
    private final Object bean;
    private final String name;

    private ObjectPropertyReference(Object bean, String name,
            Resolver<T> resolver) {
        this.bean = bean;
        this.name = name;
        this.resolver = resolver;
    }

    public T dereference() {
        if (bean == null) {
            return null;
        } else {
            return resolver.dereference(bean, name);
        }
    }

}

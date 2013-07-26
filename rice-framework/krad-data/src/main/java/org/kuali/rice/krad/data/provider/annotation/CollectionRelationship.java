package org.kuali.rice.krad.data.provider.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.rice.krad.data.metadata.DataObjectCollection;

/**
 * Defines that the associated Collection field contains a collection of DataObjects. Analog to the
 * {@link DataObjectCollection} metadata.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectionRelationship {
	/**
	 * The element type of the collection. If the collection contains Generics, it will be derived automatically.
	 */
	Class<?> collectionElementClass() default Object.class;

	/**
	 * The list of attribute relationships linking the parent object and the collection objects.
	 */
	AttributeRelationship[] attributeRelationships();

	/**
	 * Default sort order for the collection.
	 */
	CollectionSortAttribute[] sortAttributes() default {};

	long minItemsInCollection() default 0L;

	long maxItemsInCollection() default Long.MAX_VALUE;

	/**
	 * Whether this collection uses an indirection table between the parent and collection objects. This has no function
	 * at present, but is here for informational purposes.
	 */
	boolean indirectCollection() default false;

	/**
	 * When needed, how to label each element of the collection. (Usually singular) Will default to the label of the
	 * contained element type.
	 */
	String elementLabel() default "";

	/**
	 * The label of the collection itself. (Usually plural)
	 */
	String label() default "";
}

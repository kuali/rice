package org.kuali.rice.core.api.mo;

/**
 * This is an interface that defines a builder.  A builder is an object used to
 * assemble and construct an instance of another object.  Typically the object
 * being constructed will be an immutable object (and therefore does not
 * contain "setters" which can be used to mutate it's state).  The builder
 * pattern is a creation pattern that can be used to aid in the construction of
 * these complex immutable objects.
 * 
 * <p>This interface only defines a common {@link #build()} method which is
 * used to return an instance of the object once state has been set on the
 * builder to a point where construction of an object instance is deemed
 * acceptable by the client code.  Definition of type-specific setter methods
 * are defined by the classes which implement this interface.
 *
 * <p>This version of the builder pattern is proposed by Joshua Bloch in his
 * book "Effective Java".  See "Effective Java" 2nd ed. page 15 for more
 * information.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface ModelBuilder {
	
	/**
	 * Returns an instance of the object being built by this builder based
	 * on the current state of the builder.  It should be possible to
	 * invoke this method more than once on the same builder.  It should
	 * never return null;
	 * 
	 * @return an instance of the object being built by this builder,
	 * should never return null
	 */
     Object build();
}

package org.kuali.rice.core.api.mo;

/**
 * This is an interface that defines a builder.
 *
 * See Effective Java 2nd ed. page 15 for more information.
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
     /*<T> T*/ Object build();
}

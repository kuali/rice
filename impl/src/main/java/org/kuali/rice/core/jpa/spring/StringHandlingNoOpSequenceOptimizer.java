/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.jpa.spring;

import java.io.Serializable;

import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.Optimizer;

/**
 * A version of a Hibernate Sequence Optimizer which can also use String as a returnClass, and which
 * doesn't do any of that snazzy HiLo stuff...
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StringHandlingNoOpSequenceOptimizer implements Optimizer {
	private Class returnClass;
	private int incrementSize;
	private long lastSourceValue = -1;
	
	/**
	 * Constructs the sequence optimizer
	 * 
	 * @param returnClass the return class
	 * @param incrementSize the increment size
	 */
	public StringHandlingNoOpSequenceOptimizer(Class returnClass, int incrementSize) {
		this.returnClass = returnClass;
		this.incrementSize = incrementSize;
	}

	/**
	 * @return the returnClass
	 */
	public Class getReturnClass() {
		return this.returnClass;
	}

	/**
	 * Overridden to return false like NoOpSequenceOptimizer does
	 * @see org.hibernate.id.enhanced.Optimizer#applyIncrementSizeToSourceValues()
	 */
	public boolean applyIncrementSizeToSourceValues() {
		return false;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.hibernate.id.enhanced.Optimizer#generate(org.hibernate.id.enhanced.AccessCallback)
	 */
	public Serializable generate(AccessCallback callback) {
		if ( lastSourceValue == -1 ) {
			while( lastSourceValue <= 0 ) {
				lastSourceValue = callback.getNextValue();
			}
		}
		else {
			lastSourceValue = callback.getNextValue();
		}
		return make( lastSourceValue );
	}

	/**
	 * Returns the incrementSize
	 * @see org.hibernate.id.enhanced.Optimizer#getIncrementSize()
	 */
	public int getIncrementSize() {
		return incrementSize;
	}

	/**
	 * Returns the lastSourceValue 
	 * @see org.hibernate.id.enhanced.Optimizer#getLastSourceValue()
	 */
	public long getLastSourceValue() {
		return lastSourceValue;
	}

	/**
	 * Converts the sequence value to the return type for this optimizer
	 * 
	 * @param value the sequence value
	 * @return the sequence value converted to the given return class
	 */
	public Serializable make(long value) {
		if ( getReturnClass() == Long.class ) {
			return new Long( value );
		}
		else if ( getReturnClass() == Integer.class ) {
			return new Integer( ( int ) value );
		}
		else if ( getReturnClass() == Short.class ) {
			return new Short( ( short ) value );
		}
		else if ( getReturnClass() == String.class ) {
			return Long.toString(value);
		}
		else {
			throw new IdentifierGenerationException( "this id generator generates long, integer, short" );
		}
	}
}

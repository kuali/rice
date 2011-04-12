package org.kuali.rice.krms.api.engine;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Specifies name and type for Terms.  
 * @author gilesp
 */
public final class TermSpecification implements Comparable<TermSpecification> {
	
	private final String name;
	private final String type;
	
	/**
	 * This constructs a TermSpecification, which defines a (blech) type of data that is most likely obtainable 
	 * through the {@link TermResolutionEngine}.  Or perhaps more accurately, it maps a kind of data item to a 
	 * specific service (a {@link TermResolver}) to resolve instances of it.
	 * 
	 * @param name
	 * @param type
	 */
	public TermSpecification(String name, String type) {
		if (name == null) throw new IllegalArgumentException("name is required");
		if (name.contains("!")) throw new IllegalArgumentException("name contains illegal character '!'");
		if (type == null) throw new IllegalArgumentException("type is required");
		if (type.contains("!")) throw new IllegalArgumentException("type contains illegal character '!'");
		this.name = name;
		this.type = type;
	}
	
	public String getName() { return name; }
	public String getType() { return type; }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermSpecification other = (TermSpecification) obj;
		return this.compareTo(other) == 0;
	}

	@Override
	public int compareTo(TermSpecification o) {
		if (o == null) return 1;
		if (this == o) return 0;

		return new CompareToBuilder()
			.append(this.name, o.name)
			.append(this.type, o.type)
			.toComparison();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(name: " + name + ", type: " + type + ")";
	}
	
}

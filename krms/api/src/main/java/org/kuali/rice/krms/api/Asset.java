package org.kuali.rice.krms.api;

/**
 * identifies a (hopefully) resolvable asset
 * @author gilesp
 *
 */
public final class Asset implements Comparable<Asset> {
	
	private final String name;
	private final String type;
	private final String comparatorHelper;
	
	public Asset(String name, String type) {
		if (name == null) throw new IllegalArgumentException("name is required");
		if (name.contains("!")) throw new IllegalArgumentException("name contains illegal character '!'");
		if (type == null) throw new IllegalArgumentException("type is required");
		if (type.contains("!")) throw new IllegalArgumentException("type contains illegal character '!'");
		this.name = name;
		this.type = type;
		// for lexicographic total ordering
		this.comparatorHelper = name + "!" + type;
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
		Asset other = (Asset) obj;
		return this.compareTo(other) == 0;
	}

	@Override
	public int compareTo(Asset o) {
		if (o == null) return 1;
		if (this == o) return 0;
		return (comparatorHelper.compareTo(o.comparatorHelper));
	}
	
	public String getComparatorHelper() {
		return comparatorHelper;
	}

	@Override
	public String toString() {
		// TODO make this pretty
		return getClass().getSimpleName()+"("+getComparatorHelper()+")";
	}
	
}

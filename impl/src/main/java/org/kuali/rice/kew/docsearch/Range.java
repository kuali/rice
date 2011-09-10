package org.kuali.rice.kew.docsearch;

public class Range {

    private String lowerBoundValue;
    private String upperBoundValue;
    private boolean lowerBoundInclusive = true;
    private boolean upperBoundInclusive = true;

    public String getLowerBoundValue() {
        return lowerBoundValue;
    }

    public void setLowerBoundValue(String lowerBoundValue) {
        this.lowerBoundValue = lowerBoundValue;
    }

    public String getUpperBoundValue() {
        return upperBoundValue;
    }

    public void setUpperBoundValue(String upperBoundValue) {
        this.upperBoundValue = upperBoundValue;
    }

    public boolean isLowerBoundInclusive() {
        return lowerBoundInclusive;
    }

    public void setLowerBoundInclusive(boolean lowerBoundInclusive) {
        this.lowerBoundInclusive = lowerBoundInclusive;
    }

    public boolean isUpperBoundInclusive() {
        return upperBoundInclusive;
    }

    public void setUpperBoundInclusive(boolean upperBoundInclusive) {
        this.upperBoundInclusive = upperBoundInclusive;
    }
    
}

package org.kuali.rice.core.api.parameter;

/**
 * This is the contract for a Parameter.  The concept of a parameter is a key=value pair that is associated
 * with a rice enabled application.
 */
public interface ParameterContract {

	/**
     * This the application code for the Parameter.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the Parameter to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application code
     */
	String getApplicationCode();

    /**
     * This is the namespace for the parameter.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the parameter to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
	String getNamespaceCode();
	
	/**
     * This is the component code for the parameter.  This cannot be null.
     *
     * <p>
     * It is a way of assigning a parameter to a functional component within a rice application or rice ecosystem.
     * </p>
     *
     * @return component
     */
	String getComponentCode();
	
    /**
     * The name of the parameter.  This cannot be null or a blank string.
     * @return name
     */
    String getName();

    /**
     * The value of the parameter.  This can be null or a blank string.
     * @return value
     */
	String getValue();

    /**
     * This is the description for what the parameter is used for.  This can be null or a blank string.
     * @return description
     */
	String getDescription();

    /**
     * This is the evaluation operator for the parameter.  This can be null.
     *
     * <p>
     * This allows parameters to be used as primitive business rules.
     * </p>
     *
     * @return evaluation operator
     */
	EvaluationOperator getEvaluationOperator();

    /**
     * This is the type for the parameter.  This cannot be null.
     *
     * <p>
     * Some parameters have special types in rice which may have special meaning
     * and is related to the {@link #getEvaluationOperator()}
     * </p>
     *
     * @return type
     */
	ParameterTypeContract getParameterType();

}

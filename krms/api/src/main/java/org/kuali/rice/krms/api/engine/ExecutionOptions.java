package org.kuali.rice.krms.api.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class ExecutionOptions {

	private final Map<ExecutionFlag, Boolean> flags;
	private final Map<String, String> options;
	
	public ExecutionOptions() {
		flags = new HashMap<ExecutionFlag, Boolean>();
		options = new HashMap<String, String>();
	}
	
	public ExecutionOptions(ExecutionOptions executionOptions) {
		this.flags = new HashMap<ExecutionFlag, Boolean>(executionOptions.getFlags());
		this.options = new HashMap<String, String>(executionOptions.getOptions());
	}
	
	public ExecutionOptions setFlag(ExecutionFlag flag, boolean value) {
		if (flag == null) {
			throw new IllegalArgumentException("flag was null");
		}
		flags.put(flag, value);
		return this;
	}
	
	public ExecutionOptions addOption(String option, String value) {
		if (StringUtils.isBlank(option)) {
			throw new IllegalArgumentException("option was blank");
		}
		options.put(option, value);
		return this;
	}
	
	public ExecutionOptions removeFlag(ExecutionFlag flag) {
		if (flag == null) {
			throw new IllegalArgumentException("flag was null");
		}
		flags.remove(flag);
		return this;
	}
	
	public ExecutionOptions removeOption(String option) {
		if (StringUtils.isBlank(option)) {
			throw new IllegalArgumentException("option was blank");
		}
		options.remove(option);
		return this;
	}
	
	public boolean isFlagSet(ExecutionFlag flag, boolean defaultValue) {
		if (containsFlag(flag)) {
			return flags.get(flag).booleanValue();
		}
		return defaultValue;
	}
	
	public String getOptionValue(String option) {
		return options.get(option);
	}
	
	public boolean containsFlag(ExecutionFlag flag) {
		return flags.containsKey(flags);
	}
	
	public boolean containsOption(String option) {
		return options.containsKey(option);
	}

	public Map<ExecutionFlag, Boolean> getFlags() {
		return Collections.unmodifiableMap(flags);
	}

	
	public Map<String, String> getOptions() {
		return Collections.unmodifiableMap(options);
	}
	
}

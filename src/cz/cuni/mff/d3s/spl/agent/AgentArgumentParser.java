package cz.cuni.mff.d3s.spl.agent;

import java.util.HashMap;
import java.util.Map;

/** Simple parser of arguments passed to Java agent. */
class AgentArgumentParser {
	private static final String OPTION_DELIMITER = ",";
	private static final String KEY_VALUE_DELIMITER = "=";
	private Map<String, String> options = new HashMap<>();
	
	/** Factory method for creating new parser.
	 * 
	 * @param arguments Arguments as passed to agent premain method.
	 * @return Argument parser.
	 */
	public static AgentArgumentParser create(String arguments) {
		AgentArgumentParser parser = new AgentArgumentParser();
		parser.parse(arguments);
		return parser;
	}
	
	/** Parse agent arguments.
	 * 
	 * The newly parsed arguments are added to already existing ones
	 * (i.e. calling this method several times on different input would
	 * merge all arguments, latest overriding the first ones).
	 * 
	 * @param arguments Arguments as passed to agent premain method.
	 */
	protected void parse(String arguments) {
		if ((arguments == null) || arguments.isEmpty()) {
			return;
		}
		
		String[] parts = arguments.split(OPTION_DELIMITER);
		for (String opt : parts) {
			String[] keyValuePair = opt.split(KEY_VALUE_DELIMITER, 2);
			if (keyValuePair.length == 1) {
				options.put(keyValuePair[0], null);
			} else {
				assert keyValuePair.length == 2;
				options.put(keyValuePair[0], keyValuePair[1]);
			}
		}
	}
	
	public boolean hasOption(String key) {
		return options.containsKey(key);
	}
	
	/** Get string argument.
	 * 
	 * @param key Option (parameter) name.
	 * @param defaultValue Default value if option not present.
	 * @return String argument of given option.
	 */
	public String getValue(String key, String defaultValue) {
		String value = options.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
	
	/** Get integer argument.
	 * 
	 * If the option is present but it is not possible to convert the value
	 * to integer, default value is returned and the error is silently ignored.
	 * 
	 * @param ke Option (parameter) name.
	 * @param defaultValue Default value if option not present.
	 * @return Integer argument of given option.
	 */
	public int getValue(String key, int defaultValue) {
		String strValue = getValue(key, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}

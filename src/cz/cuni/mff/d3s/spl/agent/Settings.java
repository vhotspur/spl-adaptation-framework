package cz.cuni.mff.d3s.spl.agent;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Settings {
	/** How many invocations are skipped before one is recorded.
	 * 
	 * The default value means that 100 invocations of the
	 * instrumented function will not be measured and then the
	 * 101st would be measured and the result would be added to
	 * the respective SampleStorage.
	 */
	public static int DEFAULT_SKIP_FACTOR = 100;
	
	/** Whether to dump newly registered class-loader. */
	public static boolean DEBUG_CLASSLOADERS = false;
	
	/** Whether to print details about (first-load) transformers. */
	public static boolean DEBUG_LOADING_TRANSFORMER = false;
	
	/** Whether to print details about retransforming transformers. */
	public static boolean DEBUG_RETRANSFORMING_TRANSFORMER = false;
	
	/** Whether to print info about each class that is being loaded. */
	public static boolean DEBUG_ALL_CLASS_LOADING = false;
	
	/** Stream where to log the debugging messages. */
	public static PrintStream log = System.out;
	
	/** Whether to specifically print details about transformations on
	 * specific classes.
	 * 
	 * @see WATCHED_CLASSES
	 */
	public static boolean DEBUG_WATCH_CLASSES = false;

	/** Slash-separated set of classes that requires extra watching.
	 * 
	 * Use DEBUG_WATCH_CLASS to test whether certain class
	 * shall be watched.
	 */
	public static Set<String> WATCHED_CLASSES = new HashSet<>();
	
	/** Whether given class is marked as under detailed watch.
	 * 
	 * This function result is condition by DEBUG_WATCH_CLASSES being true.
	 * 
	 * @param classname Full class name (slash separated).
	 * @return Whether this class shall be logged in greater detail.
	 */
	public static boolean DEBUG_WATCH_CLASS(String classname) {
		return DEBUG_WATCH_CLASSES && WATCHED_CLASSES.contains(classname);
	}
}

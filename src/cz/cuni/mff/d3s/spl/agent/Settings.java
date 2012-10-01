package cz.cuni.mff.d3s.spl.agent;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Settings {
	public static int DEFAULT_SKIP_FACTOR = 100;
	public static boolean DEBUG_CLASSLOADERS = false;
	public static boolean DEBUG_LOADING_TRANSFORMER = false;
	public static boolean DEBUG_RETRANSFORMING_TRANSFORMER = false;
	public static boolean DEBUG_ALL_CLASS_LOADING = false;
	public static PrintStream log = System.out;
	public static boolean DEBUG_WATCH_CLASSES = false;
	// Slash separated!
	public static Set<String> WATCHED_CLASSES = new HashSet<>();
	public static boolean DEBUG_WATCH_CLASS(String classname) {
		return DEBUG_WATCH_CLASSES && WATCHED_CLASSES.contains(classname);
	}
}

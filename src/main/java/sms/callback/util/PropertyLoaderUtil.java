package sms.callback.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads environment specific resources (key/value) into memory. Returns
 * {@link Properties} object after successful loading.
 * 
 * @author Karan.Kapoor@aeris.net
 */
public class PropertyLoaderUtil {

	private final static String SLASH = "/";

	private PropertyLoaderUtil() {
		// empty constructor
	}

	/**
	 * This methods loads a property file.
	 * 
	 * @param fileName
	 *            name of the file to load
	 * @return {@link Properties}
	 * @throws ResourceLoadingException
	 */
	public static Properties loadProperties(String fileName) {
		final Properties propertiesToLoad = new Properties();

		fileName = getFileName(fileName);

		try {
			propertiesToLoad.load(PropertyLoaderUtil.class.getResourceAsStream(fileName));
			return propertiesToLoad;
		} catch (final FileNotFoundException e) {
			throw new RuntimeException("PropertyLoaderUtil could not load properties for file :: " + fileName, e);
		} catch (final IOException e) {
			throw new RuntimeException("PropertyLoaderUtil could not load properties for file :: " + fileName, e);
		}
	}

	/**
	 * if {@code useEnv} is {@code true} then add the environment before the
	 * file name to load environment specific file, else check if the filename
	 * starts with {@code SLASH "/"}, if not then prefix the name with
	 * {@code SLASH "/"}
	 * 
	 * @param fileName
	 *            name of the file to load
	 * @return
	 */
	private static String getFileName(String fileName) {
		if (!fileName.startsWith(SLASH)) {
			fileName = SLASH + fileName;
		}
		return fileName;
	}
}
package sms.callback.util;

import java.util.Properties;
import static sms.callback.util.IConstants.CONFIG_PROPERTIES_FILE_NAME;

/**
 * Returns the configuration values based on the key specified. Singleton class,
 * instance methods can be invoked using static getInstance method.
 * 
 * @author Karan.Kapoor@aeris.net
 * @see {@link PropertyLoaderUtil}
 *
 */
public class PropertyReaderUtil {
	private static Properties environmentProperties;

	private PropertyReaderUtil() {
		environmentProperties = PropertyLoaderUtil.loadProperties(CONFIG_PROPERTIES_FILE_NAME);
	}

	/**
	 * @param fileName
	 *            name of the file to load
	 */
	public synchronized void mergeProperties(final String fileName) {
		mergeProperties(PropertyLoaderUtil.loadProperties(fileName));
	}

	/**
	 * @param propertiesToMerge
	 */
	public synchronized void mergeProperties(final Properties propertiesToMerge) {
		if ((propertiesToMerge != null) && (propertiesToMerge.size() > 0)) {
			environmentProperties.putAll(propertiesToMerge);
		}
	}

	/**
	 * This method returns instance of property reader.
	 * 
	 * @return
	 */
	public static PropertyReaderUtil getInstance() {
		return InnerInitializationManager._readerInstance;
	}

	/**
	 * This method returns value of a property whose key matches with the
	 * {@code propertyName}.
	 * 
	 * @param propertyName
	 * @return value or null
	 */
	public String getProperty(final String propertyName) {
		final String value = environmentProperties.getProperty(propertyName);
		if (value == null) {
			//
		}
		return value;
	}

	/**
	 * This method invokes parseInt method of class {@link Integer} on the value
	 * returned by the key (only if value is not null)
	 * 
	 * @param propertyName
	 * @return Integer value or null
	 */
	public Integer getIntegerProperty(final String propertyName) {
		final String value = getProperty(propertyName);
		return (value == null) ? null : Integer.parseInt(value);
	}

	/**
	 * This method invokes parseLong method of class {@link Long} on the value
	 * returned by the key (only if value is not null)
	 * 
	 * @param propertyName
	 * @return Long value or null
	 */
	public Long getLongProperty(final String propertyName) {
		final String value = getProperty(propertyName);
		return (value == null) ? null : Long.parseLong(value);
	}

	/**
	 * This method invokes parseBoolean method of class {@link Boolean} on the
	 * value returned by the key (only if value is not null)
	 * 
	 * @param propertyName
	 * @return
	 */
	public Boolean getBooleanProperty(final String propertyName) {
		final String value = getProperty(propertyName);
		return (value == null) ? null : Boolean.parseBoolean(value);
	}

	private static class InnerInitializationManager {
		private static PropertyReaderUtil _readerInstance = createInstance();

		private static PropertyReaderUtil createInstance() {
			return new PropertyReaderUtil();
		}
	}
}
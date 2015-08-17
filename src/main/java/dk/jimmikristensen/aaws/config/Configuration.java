package dk.jimmikristensen.aaws.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jimmikristensen.aaws.domain.exception.InvalidConfigurationException;

public class Configuration {

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);
    private static final Properties properties = getConfiguration();

    private static final String CONFIGURATION_FILENAME = "/asciidoc-archive-ws.properties";

    private static Properties getConfiguration() {
        try {
            Properties loadedProperties = new Properties();
            loadedProperties.load(Configuration.class.getResourceAsStream(CONFIGURATION_FILENAME));
            return loadedProperties;
        } catch (InvalidPropertiesFormatException e) {
            log.error("Reading " + CONFIGURATION_FILENAME, e);
        } catch (FileNotFoundException e) {
            log.error("Configuration not found " + CONFIGURATION_FILENAME, e);
        } catch (IOException e) {
            log.error("Configuration error", e);
        }
        System.exit(1); // we cannot handle failures at this point todo
        return null;
    }

    /**
     * Returns a property from the configuration file
     *
     * @param key name of key
     * @param defaultValue value returned if property does not exists
     * @return value
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Returns a property from the configuration file
     *
     * @param key name of key
     * @return value
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns a property from the configuration file or fails application otherwise
     * Application dies if this property is missing from the configuration file
     *
     * @param key name of key
     * @return value
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public static String getCriticalProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.error("Vital configuration key missing", new InvalidConfigurationException("Missing configuration key " + key).fillInStackTrace());
            System.exit(1);
        }
        return value;
    }
}

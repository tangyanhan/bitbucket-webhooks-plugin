package nl.topicus.bitbucket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil
{

    private static String pluginVersion;

    public static String getVersion() {
        if (pluginVersion == null) {
            pluginVersion = getProperties().getProperty("pluginVersion", "0");
        }
        return pluginVersion;
    }

    private static Properties getProperties()
    {
        final Properties properties = new Properties();
        try (InputStream is = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("bitbucket-webhooks.properties"))
        {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e)
        {
            // ignored
        }
        return properties;
    }
}

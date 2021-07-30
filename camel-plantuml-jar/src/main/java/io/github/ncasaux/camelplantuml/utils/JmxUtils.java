package io.github.ncasaux.camelplantuml.utils;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

public class JmxUtils {
    public static JMXConnector getConnector(String jmxHost) throws IOException {
        String url = "service:jmx:rmi:///jndi/rmi://" + jmxHost + "/jmxrmi";
        JMXServiceURL serviceURL = new JMXServiceURL(url);
        return JMXConnectorFactory.connect(serviceURL);
    }
}

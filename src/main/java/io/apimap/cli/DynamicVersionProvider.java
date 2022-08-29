package io.apimap.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class DynamicVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        Enumeration<URL> resources = CommandLine.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Manifest manifest = new Manifest(url.openStream());
                Attributes attr = manifest.getMainAttributes();
                if(get(attr, "Apimap-Version") != null){
                    return new String[] { get(attr, "Apimap-Version").toString() };
                }
            } catch (IOException ignored) {
                return new String[] { "Unknown" };
            }
        }
        return new String[] { "Unknown" };
    }

    private static Object get(Attributes attributes, String key) {
        return attributes.get(new Attributes.Name(key));
    }
}

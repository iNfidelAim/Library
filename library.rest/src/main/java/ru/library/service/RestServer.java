package ru.library.service;

import java.net.URI;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RestServer {
    public static void startServer() {
        URI baseUri = URI.create("http://localhost:8080/api/");
        ResourceConfig config = new ResourceConfig().packages("ru.library.service");
        JettyHttpContainerFactory.createServer(baseUri, config);
    }
}
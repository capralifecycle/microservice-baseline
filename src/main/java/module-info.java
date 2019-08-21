// Replace the modulename with your own application specific name.
module no.capraconsulting.microservice.baseline {
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.io;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.security;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.util;
    requires jakarta.servlet.api;
    requires org.slf4j;
    requires java.management;
    requires java.ws.rs;

    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jersey.common;

    exports no.capraconsulting.health;
    exports no.capraconsulting.secured;

}

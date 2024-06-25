package org.openehealth.ipf.tutorials.config;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/10
 */

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.openehealth.ipf.commons.ihe.ws.cxf.payload.InPayloadLoggerInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.payload.OutPayloadLoggerInterceptor;
import org.openehealth.ipf.tutorials.pix.Iti44TestRouteBuilder;
import org.openehealth.ipf.tutorials.xds.Iti18RouteBuilder;
import org.openehealth.ipf.tutorials.xds.Iti4142RouteBuilder;
import org.openehealth.ipf.tutorials.xds.Iti43RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml"})
public class XdsApplicationConfig {

    @Bean(name = Bus.DEFAULT_BUS_ID)
    SpringBus springBus() {
        var springBus = new SpringBus();
        var logging = new LoggingFeature();
        logging.setLogBinary(true);
        logging.setLogMultipart(true);
        logging.setVerbose(true);
        springBus.getFeatures().add(logging);
        BusFactory.setDefaultBus(springBus);
        return springBus;
    }

    @Bean
    public Iti4142RouteBuilder iti4142RouteBuilder() {
        return new Iti4142RouteBuilder();
    }

    @Bean
    public Iti43RouteBuilder iti43RouteBuilder() {
        return new Iti43RouteBuilder();
    }

    @Bean
    public Iti18RouteBuilder iti18RouteBuilder() {
        return new Iti18RouteBuilder();
    }

    @Bean
    public String logFileNamePrefix() {
        return "/Users/bovane/Documents/hos-app/logs";
    }
    @Bean
    public InPayloadLoggerInterceptor serverInLogger() {
        return new InPayloadLoggerInterceptor("/Users/bovane/Documents/hos-app/logs/server-in.txt");
    }

    @Bean
    public OutPayloadLoggerInterceptor serverOutLogger() {
        return new OutPayloadLoggerInterceptor("/Users/bovane/Documents/hos-app/logs/server-out.text");
    }

    @Bean
    public Iti44TestRouteBuilder iti44TestRouteBuilder() {
        return new Iti44TestRouteBuilder();
    }

}

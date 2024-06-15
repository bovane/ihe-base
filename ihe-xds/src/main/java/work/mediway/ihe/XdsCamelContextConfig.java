package work.mediway.ihe;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/10
 */
import org.apache.camel.spring.SpringCamelContext;
import org.openehealth.ipf.commons.audit.DefaultAuditContext;
import org.openehealth.ipf.commons.audit.queue.RecordingAuditMessageQueue;
import org.openehealth.ipf.commons.ihe.ws.cxf.payload.InPayloadLoggerInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.payload.OutPayloadLoggerInterceptor;
import org.openehealth.ipf.tutorials.xds.DataStore;
import org.openehealth.ipf.tutorials.xds.Iti18RouteBuilder;
import org.openehealth.ipf.tutorials.xds.Iti4142RouteBuilder;
import org.openehealth.ipf.tutorials.xds.Iti43RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XdsCamelContextConfig {
    @Bean
    public DataStore dataStore() {
        return new DataStore();
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
        return new OutPayloadLoggerInterceptor("/Users/bovane/Documents/hos-app/logs/server-out.txt");
    }
}

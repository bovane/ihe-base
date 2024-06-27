package org.openehealth.ipf.tutorials.config;

import org.apache.camel.component.hl7.HL7MLLPNettyDecoderFactory;
import org.apache.camel.component.hl7.HL7MLLPNettyEncoderFactory;
import org.openehealth.ipf.commons.ihe.hl7v3.storage.Hl7v3ContinuationStorage;
import org.openehealth.ipf.commons.ihe.hl7v3.storage.SpringCacheHl7v3ContinuationStorage;
import org.openehealth.ipf.commons.ihe.ws.correlation.AsynchronyCorrelator;
import org.openehealth.ipf.commons.ihe.ws.correlation.SpringCacheAsynchronyCorrelator;
import org.openehealth.ipf.commons.spring.map.SpringBidiMappingService;
import org.openehealth.ipf.tutorials.pix.Iti44TestRouteBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
@Configuration
public class PixApplicationConfig {

    @Bean
    public Iti44TestRouteBuilder iti44TestRouteBuilder() {
        return new Iti44TestRouteBuilder();
    }

    @Bean
    public HL7MLLPNettyDecoderFactory hl7decoder() {
        HL7MLLPNettyDecoderFactory hl7decoder = new HL7MLLPNettyDecoderFactory();
        hl7decoder.setCharset(StandardCharsets.UTF_8);
        hl7decoder.setProduceString(Boolean.FALSE);
        return hl7decoder;
    }

    @Bean
    public HL7MLLPNettyEncoderFactory hl7encoder() {
        HL7MLLPNettyEncoderFactory hl7encoder = new HL7MLLPNettyEncoderFactory();
        hl7encoder.setCharset(StandardCharsets.UTF_8);
        return hl7encoder;
    }

    @Bean
    public SpringBidiMappingService mappingService() {
        SpringBidiMappingService mappingService = new SpringBidiMappingService();
        Collection<Resource> mappingResources = new ArrayList<>();
        mappingResources.add(new ClassPathResource("META-INF/map/hl7-v2-v3-translation.map"));

        mappingService.setMappingResources(mappingResources);
        return mappingService;
    }

    @Bean
    public Hl7v3ContinuationStorage hl7v3ContinuationStorage(CacheManager cacheManager) {
        return new SpringCacheHl7v3ContinuationStorage(cacheManager);
    }

    @Bean
    public AsynchronyCorrelator correlator(CacheManager cacheManager) {
        return new SpringCacheAsynchronyCorrelator(cacheManager);
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheSpecification("maximumSize=1000");
        return cacheManager;
    }

}

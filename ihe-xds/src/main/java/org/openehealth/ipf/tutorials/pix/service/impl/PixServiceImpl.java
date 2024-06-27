package org.openehealth.ipf.tutorials.pix.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.openehealth.ipf.commons.xml.CombinedXmlValidator;
import org.openehealth.ipf.tutorials.pix.dto.PixPatientFeedDTO;
import org.openehealth.ipf.tutorials.pix.service.PixService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static org.openehealth.ipf.commons.ihe.hl7v3.PIXV3.Interactions.ITI_44_PIX;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
@Service
@Slf4j
public class PixServiceImpl implements PixService, CamelContextAware {

    private CamelContext camelContext;
    private final CombinedXmlValidator validator = new CombinedXmlValidator();

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    private static final String ADD_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_REG_Maximal_Request.xml", StandardCharsets.UTF_8);
    private static final String REVISE_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_REV_Maximal_Request.xml",StandardCharsets.UTF_8);
    private static final String MERGE_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_MERGE_Maximal_Request.xml",StandardCharsets.UTF_8);

    private static final String REQUEST2 = FileUtil.readString("examples/request2.xml",StandardCharsets.UTF_8);

    @Override
    public void iti44PatientFeed(PixPatientFeedDTO pixPatientFeedDTO) throws Exception {
        pixPatientFeedDTO.setMessage(ADD_REQUEST);

        if (pixPatientFeedDTO.getValidate()) {
            validator.validate(pixPatientFeedDTO.getMessage(), ITI_44_PIX.getRequestValidationProfile());
        }
        String endpoint = String.format("xds-iti44://%s:%d/%s", pixPatientFeedDTO.getHost(), pixPatientFeedDTO.getPort(), pixPatientFeedDTO.getPathAndParameters());
        log.error("当前终端点为: " + endpoint);
        var response = send(endpoint, pixPatientFeedDTO.getMessage(), String.class);
        if (pixPatientFeedDTO.getValidate()) {
            validator.validate(response, ITI_44_PIX.getResponseValidationProfile());
        }
        log.warn(response);
    }


    private <T> T send(String endpoint, Object input, Class<T> outType) throws Exception {
        var result = send(endpoint, input);
        return result.getMessage().getBody(outType);
    }

    private Exchange send(String endpoint, Object body) throws Exception {
        Exchange exchange = new DefaultExchange(getCamelContext());
        exchange.getIn().setBody(body);
        /*
        if (headers != null && !headers.isEmpty()) {
            exchange.getIn().getHeaders().putAll(headers);
        }
        */
        try (var template = camelContext.createProducerTemplate()) {
            var result = template.send(endpoint, exchange);

            if (result.getException() != null) {
                throw result.getException();
            }
            return result;
        }
    }
}

package org.openehealth.ipf.tutorials.pix.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.tutorials.pix.dto.PixPatientFeedDTO;
import org.openehealth.ipf.tutorials.pix.service.PixService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
@Service
@Slf4j
public class PixServiceImpl implements PixService {

    private static final String ADD_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_REG_Maximal_Request.xml", StandardCharsets.UTF_8);
    private static final String REVISE_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_REV_Maximal_Request.xml",StandardCharsets.UTF_8);
    private static final String MERGE_REQUEST =
            FileUtil.readString("translation/pixfeed/v3/PIX_FEED_MERGE_Maximal_Request.xml",StandardCharsets.UTF_8);
    @Override
    public void iti44PatientFeed(PixPatientFeedDTO pixPatientFeedDTO) {



    }
}

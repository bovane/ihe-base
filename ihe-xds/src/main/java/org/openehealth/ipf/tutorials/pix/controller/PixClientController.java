package org.openehealth.ipf.tutorials.pix.controller;

import org.openehealth.ipf.tutorials.pix.dto.PixPatientFeedDTO;
import org.openehealth.ipf.tutorials.pix.service.PixService;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.result.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
@RestController
@RequestMapping("/ihe/pix/client")
public class PixClientController {
    @Resource
    private PixService pixService;

    @PostMapping(value = "pix-iti44", consumes = "application/json")
    public ResponseResult pixXdsIti44(@RequestBody PixPatientFeedDTO pixPatientFeedDTO) throws Exception {
        this.pixService.iti44PatientFeed(pixPatientFeedDTO);
        return ResponseResult.success();
    }

}

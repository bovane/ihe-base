package org.openehealth.ipf.tutorials.xds.controller;

import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.result.ResponseResult;
import org.openehealth.ipf.tutorials.xds.service.XdsClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author bovane bovane.ch@gmail.com
 * @date 2024/2/20
 */
@RestController
@RequestMapping("/ihe/xds/client")
public class XdsClientController {

    @Resource
    private XdsClientService xdsClientService;

    @PostMapping(value = "iti41-42", consumes = "application/json")
    public ResponseResult iti4142ProvidedAndRegister(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.xdsClientService.iti4142ProvidedAndRegister(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti42", consumes = "application/json")
    public ResponseResult iti42Register(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        this.xdsClientService.Iti42Register(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }


    @PostMapping(value = "iti43", consumes = "application/json")
    public ResponseResult iti43Retrieve(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.xdsClientService.Iti43Retrieve(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti18", consumes = "application/json")
    public ResponseResult iti18Query(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.xdsClientService.Iti18Query(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }






}

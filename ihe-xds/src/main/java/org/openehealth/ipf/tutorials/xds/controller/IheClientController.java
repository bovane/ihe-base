package org.openehealth.ipf.tutorials.xds.controller;

import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.result.ResponseResult;
import org.openehealth.ipf.tutorials.xds.service.IheClientService;
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
public class IheClientController {

    @Resource
    private IheClientService iheClientService;

    @PostMapping(value = "iti41-42/single", consumes = "application/json")
    public ResponseResult iti4142ProvidedAndRegister(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        this.iheClientService.iti4142JustSingleDocument(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti41-42/multiple", consumes = "application/json")
    public ResponseResult iti4142ProvidedAndRegisterMultiple(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.iheClientService.iti4142MultipleDocument(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti41-42/association", consumes = "application/json")
    public ResponseResult iti4142ProvidedAndRegisterAssociation(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.iheClientService.iti4142AssociationDocument(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti42/register", consumes = "application/json")
    public ResponseResult iti42Register(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        this.iheClientService.Iti42Register(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }


    @PostMapping(value = "iti43/retrieve", consumes = "application/json")
    public ResponseResult iti43Retrieve(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        this.iheClientService.Iti43Retrieve(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }

    @PostMapping(value = "iti18/query", consumes = "application/json")
    public ResponseResult iti18Query(@RequestBody XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        this.iheClientService.Iti18Query(xdsProvidedRegisterDTO);
        return ResponseResult.success();
    }






}

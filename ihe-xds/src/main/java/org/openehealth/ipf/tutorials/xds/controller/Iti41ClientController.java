package org.openehealth.ipf.tutorials.xds.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.tutorials.xds.dto.ProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.result.ResponseResult;
import org.openehealth.ipf.tutorials.xds.service.Iti41ClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongyj <1126834403@qq.com><br/>
 * @date 2024/7/4
 */
@Slf4j
@RestController
@RequestMapping("/ihe/xds")
@RequiredArgsConstructor
@Api(tags = "XDS-Iti41文档提供与注册")
public class Iti41ClientController {

    private final Iti41ClientService iti41ClientService;

    @ApiOperation(value = "ITI41-文档提供与注册")
    @ApiOperationSupport(order = 1)
    @PostMapping(value = "iti41", consumes = "application/json")
    public ResponseResult iti4142ProvidedAndRegister(@RequestBody ProvidedRegisterDTO providedRegisterDTO) throws Exception {
        this.iti41ClientService.CreateProvidedAndRegister(providedRegisterDTO);
        return ResponseResult.success();
    }
}

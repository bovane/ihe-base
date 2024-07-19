package org.openehealth.ipf.tutorials.xds.service;


import org.openehealth.ipf.tutorials.xds.dto.ProvidedRegisterDTO;

import java.net.MalformedURLException;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/18
 */
public interface Iti41ClientService {

    /***
     * 创建单文档的提交集
     *
     * @author bovane
     * [providedRegisterDTO]
     * @return void
     */
    void CreateProvidedAndRegister(ProvidedRegisterDTO providedRegisterDTO) throws MalformedURLException;
}

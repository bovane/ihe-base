/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.tutorials.pix

import cn.hutool.core.io.FileUtil
import org.apache.camel.builder.RouteBuilder
import org.openehealth.ipf.platform.camel.ihe.hl7v3.PixPdqV3CamelValidators
import org.openehealth.ipf.tutorials.pix.processor.Iti44Processor
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

/**
 * @author Dmytro Rud
 */
@Component
class Iti44TestRouteBuilder extends RouteBuilder {

//    private static final String ACK = FileUtil.readString('translation/pixfeed/v3/Ack.xml', StandardCharsets.UTF_8)

    @Override
    public void configure() throws Exception {
        from('pixv3-iti44:pixv3-iti44-service1')
            .process(PixPdqV3CamelValidators.iti44RequestValidator())
//            .setBody(constant(ACK))
            .process(PixPdqV3CamelValidators.iti44ResponseValidator())

        from('xds-iti44:xds-iti44-service1')
            .process(PixPdqV3CamelValidators.iti44RequestValidator())
            .process(new Iti44Processor())
//            .setBody(constant(ACK))
            .process(PixPdqV3CamelValidators.iti44ResponseValidator())

    }
}

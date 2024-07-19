package org.openehealth.ipf.tutorials.runner;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhongyj <1126834403@qq.com><br/>
 * @date 2020/1/21
 */
@Slf4j
@Component
public class StartedUpRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext context;

    @Autowired
    public StartedUpRunner(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Value("${server.port:8080}")
    private String port;
    @Value("${spring.mvc.servlet.path:/}")
    private String contextPath;


    @Override
    public void run(ApplicationArguments args) {
        if (context.isActive()) {
            ThreadUtil.sleep(1000);
            ThreadUtil.execute(() -> {
                InetAddress address = null;
                try {
                    address = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
                assert address != null;
                String url = String.format("http://%s:%s", address.getHostAddress(), port);
                String docUrl = url;
                if (StringUtils.isNotBlank(contextPath)) {
                    docUrl += contextPath;
                }
                log.info("==================================");
                log.info("系统启动完毕, 地址：{}", url);
                log.info("接口文档, 地址：{}", StrUtil.removeSuffix(docUrl, "/") + "/doc.html");
                log.info("webservice, 地址：{}", StrUtil.removeSuffix(url, "/") + "/services");
                log.info("============= 启动成功 =============");
            });
        }
    }
}


















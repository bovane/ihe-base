package org.openehealth.ipf.tutorials.config;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/17
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BeanPrinter {

    @Autowired
    private ApplicationContext context;

    public void printAllBeans() {
        String[] allBeanNames = context.getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            log.info(beanName);
        }
    }
}


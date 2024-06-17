package org.openehealth.ipf.tutorials.xds;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/17
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanPrinter {

    @Autowired
    private ApplicationContext context;

    public void printAllBeans() {
        String[] allBeanNames = context.getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}


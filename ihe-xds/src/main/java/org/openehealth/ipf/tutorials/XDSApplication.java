package org.openehealth.ipf.tutorials;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/7
 */
@MapperScan({"org.openehealth.ipf.tutorials.**.mapper"})
@SpringBootApplication
public class XDSApplication {
    public static void main(String[] args) {
        SpringApplication.run(XDSApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner run(BeanPrinter beanPrinter) {
//        return args -> beanPrinter.printAllBeans();
//    }

}
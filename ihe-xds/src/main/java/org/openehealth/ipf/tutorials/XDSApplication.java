package org.openehealth.ipf.tutorials;

import org.mybatis.spring.annotation.MapperScan;
import org.openehealth.ipf.tutorials.config.BeanPrinter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/7
 */
@MapperScan({"org.openehealth.ipf.tutorials.**.mapper"})
@SpringBootApplication
@EnableWebMvc
public class XDSApplication {
    public static void main(String[] args) {
        SpringApplication.run(XDSApplication.class, args);
    }
    @Bean
    public CommandLineRunner run(BeanPrinter beanPrinter) {
        return args -> beanPrinter.printAllBeans();
    }

}
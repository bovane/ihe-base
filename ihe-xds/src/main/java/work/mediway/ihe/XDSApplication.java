package work.mediway.ihe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/7
 */
@SpringBootApplication
public class XDSApplication {
    public static void main(String[] args) {
        SpringApplication.run(XDSApplication.class, args);
    }
    @Bean
    public CommandLineRunner run(BeanPrinter beanPrinter) {
        return args -> beanPrinter.printAllBeans();
    }

}
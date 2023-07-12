package kg.kadyrbekov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class ShineApplication {


    public static void main(String[] args) {
        SpringApplication.run(ShineApplication.class, args);
        System.out.println("Welcome colleges, project name is Shine-V1!");

    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @GetMapping
    public String welcome() {
        return "Welcome to Shine Application!";
    }



}



package kg.kadyrbekov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class ShineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShineApplication.class, args);
        System.out.println("Welcome collegus, project name is Shine-V1!");

    }

    @GetMapping
    public String welcome() {
        return "Welcome to Shine Application!";
    }

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        // Set the maximum file size and other properties
//        // if needed
//        return factory.createMultipartConfig();
//    }


}



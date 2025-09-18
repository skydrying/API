package group.api;

import group.api.controller.MainController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackageClasses = MainController.class)
public class API {

    public static void main(String[] args) {
        SpringApplication.run(API.class, args);
    }
}



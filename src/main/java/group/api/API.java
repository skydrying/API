package group.api;

import group.api.controller.MainController;
import group.api.forms.Auto;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = MainController.class)
public class API implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(API.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        Auto.main(new String[]{});
    }
}



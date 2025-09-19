package group.api;

import group.api.controller.MainController;
import group.api.forms.Auto;
import group.api.forms.Director;
import group.api.forms.Forms;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackageClasses = MainController.class)
public class API implements ApplicationRunner {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(API.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            group.api.forms.Forms.main(new String[0]);
            System.out.println("Forms started!");
        } catch (Exception e) {
            System.err.println("Error Forms: " + e.getMessage());
        }
    }
}



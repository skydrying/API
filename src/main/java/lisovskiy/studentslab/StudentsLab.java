package lisovskiy.studentslab;

import lisovskiy.studentslab.controller.MainController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackageClasses = MainController.class)
public class StudentsLab {

    public static void main(String[] args) {
        SpringApplication.run(StudentsLab.class, args);
    }
}



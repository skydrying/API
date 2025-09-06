package lisovskiy.studentslab.repository;

import lisovskiy.studentslab.entity.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Integer>{
    
}

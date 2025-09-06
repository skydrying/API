package lisovskiy.studentslab.repository;
import lisovskiy.studentslab.entity.ClassTeacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTeacherRepository extends CrudRepository<ClassTeacher, Integer>{
    
}

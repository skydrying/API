package lisovskiy.studentslab.repository;

import lisovskiy.studentslab.entity.Faculty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends CrudRepository<Faculty, Integer>{
    
}

package lisovskiy.studentslab.repository;
import lisovskiy.studentslab.entity.Director;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends CrudRepository<Director, Integer>{
    
}

package lisovskiy.studentslab.repository;


import lisovskiy.studentslab.entity.Specialty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends CrudRepository<Specialty, Integer>{
    
}

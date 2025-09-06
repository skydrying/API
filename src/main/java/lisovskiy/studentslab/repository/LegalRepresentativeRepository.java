package lisovskiy.studentslab.repository;


import lisovskiy.studentslab.entity.LegalRepresentative;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalRepresentativeRepository extends CrudRepository<LegalRepresentative, Integer>{
    
}

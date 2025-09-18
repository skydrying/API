package group.api.repository;
import group.api.entity.Productionmaster;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionmasterRepository extends CrudRepository<Productionmaster, Integer>{
    
}

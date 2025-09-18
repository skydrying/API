package group.api.repository;
import group.api.entity.EmbroideryKit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbroideryKitRepository extends CrudRepository<EmbroideryKit, Integer>{
    
}

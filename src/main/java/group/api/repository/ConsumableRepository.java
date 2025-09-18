package group.api.repository;
import group.api.entity.Consumable;
import group.api.entity.CustomFrameOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumableRepository extends CrudRepository<Consumable, Integer>{
    
}

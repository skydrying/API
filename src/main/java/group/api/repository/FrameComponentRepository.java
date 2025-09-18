package group.api.repository;
import group.api.entity.FrameComponent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameComponentRepository extends CrudRepository<FrameComponent, Integer>{
    
}

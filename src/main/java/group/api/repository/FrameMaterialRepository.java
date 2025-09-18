package group.api.repository;
import group.api.entity.FrameMaterial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameMaterialRepository extends CrudRepository<FrameMaterial, Integer>{
    
}

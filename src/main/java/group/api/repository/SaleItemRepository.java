package group.api.repository;
import group.api.entity.SaleItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends CrudRepository<SaleItem, Integer>{
    
}

package lisovskiy.studentslab.repository;


import lisovskiy.studentslab.entity.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
    
}

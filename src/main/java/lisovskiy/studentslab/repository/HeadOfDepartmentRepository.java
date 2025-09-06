
package lisovskiy.studentslab.repository;


import lisovskiy.studentslab.entity.HeadOfDepartment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadOfDepartmentRepository extends CrudRepository<HeadOfDepartment, Integer>{
    
}

package lisovskiy.studentslab.repository;

import lisovskiy.studentslab.entity.Feedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {
    
}

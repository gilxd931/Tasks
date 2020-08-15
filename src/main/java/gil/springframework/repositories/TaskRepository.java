package gil.springframework.repositories;

import gil.springframework.domain.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task,Long> {
}

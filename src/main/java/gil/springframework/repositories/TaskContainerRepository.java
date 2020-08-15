package gil.springframework.repositories;

import gil.springframework.domain.TaskContainer;
import org.springframework.data.repository.CrudRepository;

public interface TaskContainerRepository extends CrudRepository<TaskContainer, Long> {
}

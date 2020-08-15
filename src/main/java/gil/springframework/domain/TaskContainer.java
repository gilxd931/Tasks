package gil.springframework.domain;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TaskContainer {

    public TaskContainer() { // for serialization
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 250)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taskContainer") // means it cannot be exist without the container.
    private List<Task> tasksList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasksList() {
        return tasksList;
    }

    public void setTasksList(List<Task> tasksList) {
        this.tasksList = tasksList;
    }

    @Override
    public String toString() {
        return "TaskContainer{" +
                "name='" + name + '\'' +
                '}';
    }
}

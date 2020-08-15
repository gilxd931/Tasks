package gil.springframework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Task implements  Comparable<Task>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 250)
    private String name;

    @Size(max = 250)
    private String description;

    private Date dueDate;
    private Priority priority;

    public Task(String name, String description, Date dueDate, TaskContainer taskContainer, Priority priority){
        this.description = description;
        this.name = name;
        this.dueDate = dueDate;
        this.taskContainer = taskContainer;
        this.priority = priority;
    }

    public Task(){} // for serialization

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskContainer getTaskContainer() {
        return taskContainer;
    }

    public void setTaskContainer(TaskContainer taskContainer) {
        this.taskContainer = taskContainer;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @ManyToOne(targetEntity = TaskContainer.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JsonIgnore
    private TaskContainer taskContainer;

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                '}';
    }

    @Override
    public int compareTo(Task t) {
        /**
         * Compares a task. first by priority, and then by date, reversed. for keeping the order in a task container
         */
        Integer  t1Pri = this.getPriority().ordinal();
        Integer  t2Pri = t.getPriority().ordinal();
        int priComp = t1Pri.compareTo(t2Pri);
        if (priComp != 0)
            return priComp;

        Date  date1 = this.getDueDate();
        Date  date2 = t.getDueDate();

        int datesCompare= date1.compareTo(date2);
        if (datesCompare != 0)
            datesCompare = -datesCompare;

        return datesCompare;
    }
}


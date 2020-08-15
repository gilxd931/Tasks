package gil.springframework.bootstrap;

import gil.springframework.domain.*;
import gil.springframework.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;

@Component
public class TasksBootstrap implements CommandLineRunner {

    private final TaskContainerRepository taskContainerRepository;

    public TasksBootstrap(TaskContainerRepository taskContainerRepository) {
        this.taskContainerRepository = taskContainerRepository;
    }

    private List<TaskContainer> initTasks() {
        /**
         * Initialize the DB with 4 tasks in container1, and 1 task in container2
         */
        List<TaskContainer> containers = new ArrayList<>();

        TaskContainer container1 = new TaskContainer();
        container1.setName("container1");
        List<Task> tasks1 = new ArrayList<>();
        Task task1 = new Task("task1", "task1 desc", new Date(currentTimeMillis() - 100000000), container1, Priority.low);
        Task task2 = new Task("task2", "task2 desc",  new Date(currentTimeMillis() - 500000000), container1, Priority.low);
        Task task3 = new Task("task3", "task3 desc",  new Date(currentTimeMillis() - 1000000000), container1, Priority.medium);
        Task task4 = new Task("task4", "task4 desc",  new Date(currentTimeMillis() - 60000000), container1, Priority.high);
        tasks1.add(task1);
        tasks1.add(task2);
        tasks1.add(task3);
        tasks1.add(task4);
        container1.setTasksList(tasks1);

        containers.add(container1);

        TaskContainer container2 = new TaskContainer();
        container2.setName("container2");
        Task task5 = new Task("task5", "task5 desc", new Date(currentTimeMillis() - 4000000), container2, Priority.low);
        List<Task> tasks2 = new ArrayList<>();
        tasks2.add(task5);
        container2.setTasksList(tasks2);
        containers.add(container2);

        return containers;
    }

    @Override
    public void run(String... args) throws Exception {
        taskContainerRepository.saveAll(initTasks());
    }
}
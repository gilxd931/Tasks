package gil.springframework.services;

import gil.springframework.domain.Priority;
import gil.springframework.domain.Task;
import gil.springframework.domain.TaskContainer;
import gil.springframework.repositories.TaskContainerRepository;
import gil.springframework.repositories.TaskRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskContainerRepository taskContainerRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskContainerRepository taskContainerRepository) {
        this.taskRepository = taskRepository;
        this.taskContainerRepository = taskContainerRepository;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        // get all existing tasks
        taskRepository.findAll().iterator().forEachRemaining(tasks::add);

        return tasks;
    }

    @Override
    public Task deleteTask(Long containerId, Long id) {
        Optional<TaskContainer> taskContainer = taskContainerRepository.findById(containerId);

        if (taskContainer.isPresent()){
            Task task = getTaskFromContainer(id, taskContainer);
            if (task == null) return null;

            taskRepository.delete(task);
            taskContainerRepository.save(taskContainer.get());

            return task;
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "container not found"
            );
        }
    }

    private Task getTaskFromContainer(Long id, Optional<TaskContainer> taskContainer) {
        /*
         get a single task from the container tasks list, if exist
         */
        boolean found = false;
        List tasks =  taskContainer.get().getTasksList();
        int i=0;
        Task task = null;

        // try to find the task in the container
        while (i < tasks.size() && !found){
            task = (Task) tasks.get(i);
            if (task.getId() == id) {
                found = true;
                taskContainer.get().getTasksList().remove(i);
                break;
            }
            i++;
        }
        if (!found)
            return null;
        return task;
    }

    private void updateTask(Long taskId, Priority priority) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent())
        {
            task.get().setPriority(priority);
            taskRepository.save(task.get());
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found"
            );
        }
    }

    @Override
    public void updateTask(Long containerId, Long taskId, JSONObject taskChanges) throws JSONException {
        Optional<TaskContainer> taskContainer = taskContainerRepository.findById(containerId);

        // check if task is in the container
        if (taskContainer.isPresent()){
            Task taskInContainer = getTaskFromContainer(taskId, taskContainer);

            if (taskInContainer == null){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "task not found"
                );
            }

            // if task is in the container update the relevant fields
            String name = taskChanges.getString("name");
            String description = taskChanges.getString("description");

            if (name != null){
                taskInContainer.setName(name);
            }
            if (description != null){
                taskInContainer.setDescription(description);
            }

            taskRepository.save(taskInContainer);
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "container not found"
            );
        }
    }

    @Override
    public List<Task> getContainerTasks(Long containerId) {
        Optional<TaskContainer> taskContainer = taskContainerRepository.findById(containerId);
        if (taskContainer.isPresent()) {
            // sort the tasks of the container by priority first, and date seconds
            Collections.sort(taskContainer.get().getTasksList(), new Comparator<Task>(){
                public int compare(Task t1, Task t2){
                    return t1.compareTo(t2);
                    }
                });
            return taskContainer.get().getTasksList();
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "container not found"
        );
    }

    @Override
    public Task getTask(Long containerId, Long taskId) {
        Optional<TaskContainer>  container = taskContainerRepository.findById(containerId);
        Task task;
        if (container.isPresent()){
            task = getTaskFromContainer(taskId, container);

            if (task == null){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "task was not found in the container"
                );
            }

        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "container not found"
            );
        }

        return task;
    }

    @Override
    public TaskContainer getContainer(Long containerId) {
        Optional<TaskContainer> taskContainer =  taskContainerRepository.findById(containerId);
        if (taskContainer.isPresent()){
            return taskContainer.get();
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "container not found"
        );
    }

    @Override
    public void saveContainer(TaskContainer taskContainer) {
        taskContainerRepository.save(taskContainer);
    }

    @Override
    public void createContainer(String name) {
        TaskContainer container = new TaskContainer();
        container.setName(name);
        taskContainerRepository.save(container);
    }

    @Override
    public void deleteTaskContainer(Long taskContainerId) {
        Optional<TaskContainer> taskContainer =  taskContainerRepository.findById(taskContainerId);
        if (taskContainer.isPresent()){
            taskContainerRepository.delete(taskContainer.get());
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "container not found"
            );
        }

    }

    @Override
    public void createTask(JSONObject task) throws JSONException {
        // parse json to Task object
        String pri = task.getString("priority");
        String name = task.getString("name");
        String description = task.getString("description");
        Date now = new Date();
        Priority priority = Enum.valueOf(Priority.class, pri);
        Long containerId = task.getLong("containerId");

        TaskContainer container = this.getContainer(containerId);

        Task newTask = new Task(name, description, now, container, priority);
        for (int i=0; i< container.getTasksList().size(); i++){
            if (newTask.getPriority() == container.getTasksList().get(i).getPriority()){
                container.getTasksList().add(i, newTask);
                break;
            }
        }
        this.saveContainer(container);
    }

    @Override
    public void createTaskContainer(JSONObject taskContainer) throws JSONException {
        String name = taskContainer.getString("name");
        this.createContainer(name);
    }

    @Override
    public void updateTaskPriority(JSONObject task) throws JSONException {
        Long taskId =  Long.parseLong(task.getString("id"));
        String pri = task.getString("priority");
        Priority priority =  Enum.valueOf(Priority.class, pri);
        this.updateTask(taskId, priority);
    }

}

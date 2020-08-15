package gil.springframework.services;

import gil.springframework.domain.Task;
import gil.springframework.domain.TaskContainer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface TaskService {

    List<Task> getTasks();
    Task deleteTask(Long containerId, Long id);
    void updateTask(Long containerId, Long taskId, JSONObject task) throws JSONException;
    List<Task> getContainerTasks(Long containerId);
    Task getTask(Long containerId, Long taskId);
    TaskContainer getContainer(Long containerId);
    void saveContainer(TaskContainer taskContainer);
    void createContainer(String name);
    void deleteTaskContainer(Long taskContainerId);
    void createTask(JSONObject task) throws JSONException;
    void createTaskContainer(JSONObject taskContainer) throws JSONException;
    void updateTaskPriority(JSONObject task) throws JSONException;
}

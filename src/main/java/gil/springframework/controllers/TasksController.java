package gil.springframework.controllers;

import gil.springframework.domain.Task;
import gil.springframework.services.TaskService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class TasksController {

    private final TaskService taskService;

    public TasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping({"", "/"})
    public String displayTaskContainers(Model model){
        /*
         Gets a list of all the tasks and display it on the screen by using thymeleaf
         */

        model.addAttribute("tasks", taskService.getTasks());
        return "index";
    }

    @GetMapping("/task/{containerId}/{taskId}")
    @ResponseBody
    public ResponseEntity getTaskByContainer(@PathVariable("containerId") Long containerId,
                                             @PathVariable("taskId") Long taskId){
       /*
       returns a task by a specific container
        */
        return new ResponseEntity(taskService.getTask(containerId, taskId), HttpStatus.OK);
    }

    @GetMapping("/tasks/{containerId}")
    @ResponseBody
    public ResponseEntity getContainerTasks(@PathVariable("containerId") Long containerId){
       /*
       returns a list of all the tasks of a specific container in order
        */
        return new ResponseEntity(taskService.getContainerTasks(containerId), HttpStatus.OK);
    }

    @PostMapping("/createTask")
    public ResponseEntity createTask(@RequestBody String body) {
        /*
        Create a new task (by name, description and priority) in a specific container
         */

        try {
            JSONObject jsonObject = new JSONObject(body);
            taskService.createTask(jsonObject);

        }
        catch (IllegalArgumentException | JSONException err){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wrong input"
            );
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/createContainer")
    public ResponseEntity createContainer(@RequestBody String body){
        /*
        Create a new container (by name)
         */
        try {
            JSONObject jsonObject = new JSONObject(body);
            taskService.createTaskContainer(jsonObject);

        }
        catch (IllegalArgumentException | JSONException err){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wrong input"
            );
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/updateTaskPriority")
    public ResponseEntity updateTaskPriority(@RequestBody String body){
       /*
       Update an exist task's priority (by id and priority)
        */
        try{
            JSONObject jsonObject = new JSONObject(body);
            taskService.updateTaskPriority(jsonObject);
        }catch (IllegalArgumentException | JSONException err){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wrong input"
            );
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteTask/{containerId}/{taskId}")
    public ResponseEntity deleteTask(@PathVariable("containerId") Long containerId, @PathVariable("taskId") Long taskId) {
      /*
      Delete a task in a container
       */
        Task task = taskService.deleteTask(containerId, taskId);

        if (task == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found"
            );
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteTaskContainer/{containerId}")
    public ResponseEntity deleteTaskContainer(@PathVariable("containerId") Long containerId) {
      /*
      Delete a task container, includes all tasks in the container
       */
        taskService.deleteTaskContainer(containerId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/updateTask/{containerId}/{taskId}")
    public ResponseEntity updateTask(@PathVariable("containerId") Long containerId, @PathVariable("taskId")
            Long taskId,@RequestBody String body){
        /*
        Update a task (by name or description) in a container
         */
        try{
            JSONObject jsonObject = new JSONObject(body);
            taskService.updateTask(containerId, taskId, jsonObject);

        }catch (JSONException err){
            System.out.println(err.getMessage());
        }
        catch ( IllegalArgumentException err){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task does not exist in the container"
            );
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }
}

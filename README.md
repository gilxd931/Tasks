# Spring Boot Tasks Application

Gil tasks app spring

Instructions:
1. Clone the repository
2. run maven clean, package
3. run "docker build -t 'image name' .
4. run "docker run -p 8888:8888 'image name'


* 			 The home page (index) contains a thymeleaf UI page to improve the visibility and process easier to understand


Assumptions and notes-
1. Task order kept while using the get API's (not for the UI)
2. update task is just for name and description (cannot update the container as asked in the instructions, and priority is set by another api call)
3. Metrics can be found on http://localhost:8888/actuator/prometheus
4. I used in memory DB- H2Database 
5. I used a bootstrap to initialize 5 different tasks when the application starts 

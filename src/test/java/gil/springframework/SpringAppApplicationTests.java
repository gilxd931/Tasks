package gil.springframework;

import gil.springframework.domain.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.hamcrest.CoreMatchers.equalTo;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)

@Transactional
public class SpringAppApplicationTests {

	@Autowired
	Environment environment;
	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testGetTaskFromContainer() {
		String localPort = environment.getProperty("local.server.port");
		String containerId = "1";
		String taskId= "3";

		ResponseEntity<Task> responseEntity = restTemplate.exchange(
				"http://localhost:"+ localPort + "/task/"+ containerId + "/"+ taskId, HttpMethod.GET, null,
				new ParameterizedTypeReference<Task>() {
				});

		Assert.assertThat(responseEntity.getBody().getName(), equalTo("task3"));
	}

	@Test
	public void testGetTaskFromWrongContainer() {
		String localPort = environment.getProperty("local.server.port");

		String containerId = "2";
		String taskId= "3";

		ResponseEntity<Task> responseEntity = restTemplate.exchange(
				"http://localhost:"+ localPort + "/task/"+ containerId + "/"+ taskId, HttpMethod.GET, null,
				new ParameterizedTypeReference<Task>() {
				});

		Assert.assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

	}

	@Test
	public void testCreateNewTaskInContainer(){
		String localPort = environment.getProperty("local.server.port");

		//create new task in container 2 with id 6 expected
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

		map.add("name", "name");
		map.add("description", "description");
		map.add("priority", "low");
		map.add("containerId", "2");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange
				("http://localhost:"+ localPort + "/createTask", HttpMethod.POST, request, String.class);

//		Assert.assertThat(responseEntity.getBody(), equalTo("\"OK\""));

		// validate task has been added
		String expectedContainerId = "2";
		String expectedTaskId= "6";
		ResponseEntity<Task> getResponseEntity = restTemplate.exchange(
				"http://localhost:"+ localPort + "/task/"+ expectedContainerId + "/"+ expectedTaskId, HttpMethod.GET, null,
				new ParameterizedTypeReference<Task>() {
				});

//		Assert.assertThat(getResponseEntity.getStatusCode(), equalTo(HttpStatus.OK));

	}
}

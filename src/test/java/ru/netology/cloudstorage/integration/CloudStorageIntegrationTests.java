package ru.netology.cloudstorage.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:/test-application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageIntegrationTests {

    @LocalServerPort
    private Integer port;

    private static RestTemplate restTemplate = new RestTemplate();

    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:latest"
    ).withDatabaseName("cloud");

    private static String jwt = "";
    private static String fileName = "testfile";
    private static String fileJsonBody = "{\"fileName\": \"filetest\"}";

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
        mySQLContainer.close();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    @Order(1)
    public void whenLoginWithValidCredentialsThenOkAndJWT() throws JsonProcessingException {
        // JSON-тело запроса
        String credentialsJsonBody = "{\"login\": \"kkk@kkk.org\", \"password\": 100}";

        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/login",
                HttpMethod.POST,
                getJsonRequestEntity(jwt, credentialsJsonBody),
                String.class
        );

        // Проверка статуса ответа (ожидаем HTTP 200 OK)
        assertEquals(200, response.getStatusCodeValue());

        // Парсинг тела ответа
        JsonNode jsonNode = parseResponseBody(response);

        // Проверка на содержание заголовка auth-token и на наличие его содержания
        assertTrue(jsonNode.has("auth-token"));
        assertFalse(jsonNode.get("auth-token").asText().isEmpty());

        jwt = jsonNode.get("auth-token").asText();
    }

    @Test
    @Order(2)
    public void whenUploadFileThenOk() throws IOException {
        // Заголовки для запроса
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("auth-token", "Bearer " + jwt);
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Прикрепление файла
        Resource resource = new ClassPathResource("testfile.txt"); // Файл, который хотите загрузить
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(resource.getFile()));

        // Объект HttpEntity с заголовками и телом запроса
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, requestHeaders);

        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename={filename}",
                HttpMethod.POST,
                requestEntity,
                String.class,
                getParam("filename", fileName)
        );

        // Проверка статуса ответа (ожидаем HTTP 200 OK)
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @Order(3)
    void whenDownloadFileThenReturnFile() {
        // Выполнение GET-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename={filename}",
                HttpMethod.GET,
                getRequestEntity(jwt),
                String.class,
                getParam("filename", fileName)
        );

        // Проверка статуса ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверка содержания ответа
        assertEquals("test", response.getBody());
    }

    @Test
    @Order(4)
    void whenGetAllFilesThenReturnList() throws JsonProcessingException {
        // Выполнение GET-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/list?limit={limit}",
                HttpMethod.GET,
                getRequestEntity(jwt),
                String.class,
                getParam("limit", "2")
        );

        // Парсинг тела ответа
        JsonNode fileNode = parseResponseBody(response).get(0);

        // Проверка статуса ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("testfile", fileNode.get("fileName").asText());
        assertEquals(4, fileNode.get("size").asInt());
    }

    @Test
    @Order(5)
    public void whenEditFileNameThenOk() {
        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename={filename}",
                HttpMethod.PUT,
                getJsonRequestEntity(jwt, fileJsonBody),
                String.class,
                getParam("filename", fileName)
        );

        // Проверка статуса ответа (ожидаем HTTP 200 OK)
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @Order(6)
    public void whenUserDeleteFileThenOk() {
        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/file?filename={filename}",
                HttpMethod.DELETE,
                getRequestEntity(jwt),
                String.class,
                getParam("filename", fileName)
        );

        // Проверка статуса ответа (ожидаем HTTP 200 OK)
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @Order(7)
    public void whenAuthUserLogoutThenOk() throws JsonProcessingException {
        // Отправка POST-запроса
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/logout",
                HttpMethod.POST,
                getRequestEntity(jwt),
                String.class
        );

        // Проверка статуса ответа (ожидаем HTTP 200 OK)
        assertEquals(200, response.getStatusCodeValue());
    }

    private HttpEntity<String> getRequestEntity(String jwt) {
        // Заголовки для запроса
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("auth-token", "Bearer " + jwt);

        // Объект HttpEntity с заголовками и телом запроса
        return new HttpEntity<>(requestHeaders);
    }

    private HttpEntity<String> getJsonRequestEntity(String jwt, String jsonBody) {
        // Заголовки для запроса
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("auth-token", "Bearer " + jwt);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Объект HttpEntity с заголовками и телом запроса
        return new HttpEntity<>(jsonBody, requestHeaders);
    }

    private Map<String, String> getParam(String name, String value) {
        // Добавляем объект с параметром filename
        Map<String, String> params = new HashMap<>();
        params.put(name, value);
        return params;
    }

    private JsonNode parseResponseBody(ResponseEntity<String> response) throws JsonProcessingException {
        // Парсинг тела ответа
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parsedBody = objectMapper.readTree(responseBody);
        return parsedBody;
    }

}

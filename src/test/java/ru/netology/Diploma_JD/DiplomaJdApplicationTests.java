package ru.netology.Diploma_JD;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.entity.CloudFile;
import ru.netology.Diploma_JD.repository.CloudFileRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DiplomaJdApplicationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CloudFileRepository cloudRepository;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.1");

    @DynamicPropertySource // заполняю инстанс БД свойствами, чтобы можно было к ней подключиться
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
    }

    @Test
    @Order(value = 1)
    public void testLogin200_OK() throws Exception {
        JwtRequest user1 = new JwtRequest("user1@mail.ru", "123");
        String userStr1 = objectMapper.writeValueAsString(user1);

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON).content(userStr1))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(value = 2)
    public void testLogin401_err() throws Exception {
        JwtRequest user2 = new JwtRequest("anybody@mail.ru", "xxx");
        String userStr2 = objectMapper.writeValueAsString(user2);

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON).content(userStr2))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(value = 3)
    @WithMockUser // создает пользователя, который уже прошел аутентификацию
    public void testGetAllFiles200_OK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:/cloud/list?limit=3"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(value = 4)
    public void testGetAllFiles401_err() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:/cloud/list?limit=3"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(value = 5)
    @WithMockUser(username = "user1@mail.ru", password = "123")
    public void testGetAllFiles1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:/cloud/list?limit=3"))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertEquals("Тест1.txt", cloudRepository.findAllByUsername_UserName("user1@mail.ru").get()
                .get(0).getFilename());
        Assertions.assertEquals(1, cloudRepository.findAllByUsername_UserName("user1@mail.ru").get().size());
    }

    @Test
    @Order(value = 6)
    @WithMockUser(username = "user1@mail.ru", password = "123") // разбить этот тест на несколько
    public void testDeleteFile() throws Exception {
        List<CloudFile> listBefore = new ArrayList<>();
        cloudRepository.findAll().forEach(listBefore::add);

        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:/cloud/file?filename=Тест1.txt"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:/cloud/file?filename=Smth.txt"))
                .andExpect(status().is4xxClientError());

        List<CloudFile> listAfter = new ArrayList<>();
        cloudRepository.findAll().forEach(listAfter::add);

        Assertions.assertEquals(0, cloudRepository.findAllByUsername_UserName("user1@mail.ru").get().size());
        Assertions.assertEquals(2, listBefore.size());
        Assertions.assertEquals(1, listAfter.size());
    }

    @Test
    @Order(value = 7)
    @WithMockUser(username = "user2@mail.ru", password = "123")
    public void testEditeFilename() throws Exception {
        FileName newName = new FileName("Test.txt");
        String newNameStr = objectMapper.writeValueAsString(newName);

        mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:/cloud/file?filename=Тест2.txt")
                        .contentType(MediaType.APPLICATION_JSON).content(newNameStr))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertTrue(cloudRepository.findAllByUsername_UserName("user2@mail.ru").get().get(0).getFilename()
                .contains("Test.txt"));
        Assertions.assertFalse(cloudRepository.findAllByUsername_UserName("user2@mail.ru").get().get(0).getFilename()
                .contains("Тест2.txt"));
    }

// Тест ниже не запустился, выходит ошибка:
//   13:37:11.265 [Test worker] INFO tc.cloud:latest -- Container cloud:latest is starting:2aaee34262352f713e734042fb1ce291258b6eac50433288d9fc78bfa2370269
//   13:37:13.819 [testcontainers-wait-0] WARN org.testcontainers.containers.wait.internal.InternalCommandPortListeningCheck -- An exception while executing the internal check: Container.ExecResult(exitCode=137, stdout=, stderr=/bin/sh: nc: command not found
//   /bin/bash: connect: Cannot assign requested address
//   /bin/bash: /dev/tcp/localhost/8080: Cannot assign requested address
//   /bin/sh: nc: command not found
//      и т.д.
//    ...
//    org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://localhost:61895/cloud/login": Connection refused
//
//
//    @Autowired
//    private TestRestTemplate restTemplate; // для получения доступа к ResponseEntity<>
//
//    @Container
//    private static final GenericContainer<?> myApp = new GenericContainer<>("cloud")
//            .withExposedPorts(8080);
//
//    @Test
//    void contextLoads() {
//        JwtRequest user1 = new JwtRequest("user1@mail.ru", "123");
//
//        ResponseEntity<JwtResponse> responseLogin = restTemplate.postForEntity("http://localhost:" + myApp.getMappedPort(8080) + "/cloud/login", user1, JwtResponse.class);
//        Assertions.assertEquals(HttpStatus.OK, responseLogin.getStatusCode());
//    }
}


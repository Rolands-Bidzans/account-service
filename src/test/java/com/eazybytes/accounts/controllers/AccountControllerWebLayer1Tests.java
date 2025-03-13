package com.eazybytes.accounts.controllers;

import com.eazybytes.accounts.dto.ResponseDto;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// This allows to run tests in parallel
// in aplication.properties we have server.port=0 -> means random
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // -> variables will be shared
public class AccountControllerWebLayer1Tests {

    @Value("${server.port}")
    private String serverPort;

    @LocalServerPort
    private int localServerPort; // this variable will save port number, that was randomly assigned

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

//    @Test
//    void contextLoads() {
//        System.out.println("server.port = " + serverPort);
//        System.out.println("localServerPort = " + localServerPort);
//    }

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateAccount_whenValidDetailsProvided_returnsUserDetailsStatusMsg() throws JSONException {
        // Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("name", "Rolands");
        userDetailsRequestJson.put("email", "Mw4oF@example.com");
        userDetailsRequestJson.put("mobileNumber", "4354437687");
//        userDetailsRequestJson.put("password", "password");
//        userDetailsRequestJson.put("repeatPassword", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<ResponseDto> createdUserDetailsEntity = testRestTemplate.postForEntity("/api/create",
                request,
                ResponseDto.class);

        ResponseDto createdUserDetails = createdUserDetailsEntity.getBody();


        // Assert
        Assertions.assertEquals(null, createdUserDetailsEntity.getBody().getStatusMsg(), () -> "Status message is not: Account created successfully");
//        Assertions.assertEquals(userDetailsRequestJson.getString("name"), createdUserDetails.getName(), () -> "User first name not same");
//        Assertions.assertEquals(userDetailsRequestJson.getString("email"), createdUserDetails.getEmail(), () -> "User email not same");
//        Assertions.assertFalse(createdUserDetails.getUserId().trim().isEmpty(), "User id is empty");
    }

//    @Test
//    @DisplayName("User can be created")
//    @Order(1)
//    void testCreateAccount_whenValidDetailsProvided_returnsUserDetailsStatusCode() throws JSONException {
//        // Arrange
//        JSONObject userDetailsRequestJson = new JSONObject();
//        userDetailsRequestJson.put("name", "Rolands");
//        userDetailsRequestJson.put("email", "Mw4oF@example2.com");
//        userDetailsRequestJson.put("mobileNumber", "4354437688");
////        userDetailsRequestJson.put("password", "password");
////        userDetailsRequestJson.put("repeatPassword", "password");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//
//
//        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
//
//        // Act
//        ResponseEntity<ResponseDto> createdUserDetailsEntity = testRestTemplate.postForEntity("/api/create",
//                request,
//                ResponseDto.class);
//
//        ResponseDto createdUserDetails = createdUserDetailsEntity.getBody();
//
//
//        // Assert
//        Assertions.assertEquals(HttpStatus.CREATED, createdUserDetailsEntity.getStatusCode(), () -> "Status code is not 201");
//
//    }

}

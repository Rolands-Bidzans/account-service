package com.eazybytes.accounts.controllers.integration.order;

import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerIntegrationTests {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TestRestTemplate testRestTemplate; // Replaces mocked WebClient

    String validAccountNumber = "123456789";
    String orderId;

    @Test
    @Order(1)
    public void testPlaceOrder_whenValidRequest_returns201() {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setName("Test Order");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("Pending");
        orderDto.setAccountNumber(validAccountNumber);

        String receiverEmail = "test@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/orders/create?receiverEmail=" + receiverEmail,
                HttpMethod.POST,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<ResponseDto>() {});


        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("201", response.getBody().getStatusCode());
        Assertions.assertEquals("Order created successfully", response.getBody().getStatusMsg());
    }

    @Test
    @Order(2)
    public void testPlaceOrder_whenInvalidNameProvidedRequest_returnsEmptyNameError400() throws JsonProcessingException {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setName("");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("Pending");
        orderDto.setAccountNumber(validAccountNumber);

        String receiverEmail = "test@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/orders/create?receiverEmail=" + receiverEmail,
                HttpMethod.POST,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                String.class);

        ObjectMapper objectMapper = new ObjectMapper(); // Jackson JSON Mapper
        Map<String, String> errorResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(errorResponse.get("name"), "Name can not be a null or empty");
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @Order(3)
    public void testPlaceOrder_whenInvalidStatusProvidedRequest_returnsInvalidStatusError400() throws JsonProcessingException {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setName("Rolands");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("");
        orderDto.setAccountNumber(validAccountNumber);

        String receiverEmail = "test@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/orders/create?receiverEmail=" + receiverEmail,
                HttpMethod.POST,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                String.class);

        ObjectMapper objectMapper = new ObjectMapper(); // Jackson JSON Mapper
        Map<String, String> errorResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(errorResponse.get("status"), "Status can not be a null or empty");
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    public void testPlaceOrder_whenInvalidAccountNumberProvidedRequest_returnsInvalidAccountNumberError400() throws JsonProcessingException {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setName("Rolands");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("Pending");
        orderDto.setAccountNumber("");

        String receiverEmail = "test@example.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/orders/create?receiverEmail=" + receiverEmail,
                HttpMethod.POST,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                String.class);

        ObjectMapper objectMapper = new ObjectMapper(); // Jackson JSON Mapper
        Map<String, String> errorResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(errorResponse.get("accountNumber"), "Account Number can not be a null or empty");
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    public void testFetchAllOrders_whenValidRequest_returnsAllOrders200() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
//        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<Set<OrdersDto>> response = testRestTemplate.exchange(
                "/api/orders/fetchAll?accountNumber=" + validAccountNumber,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<OrdersDto>>() {}
        );

        LOGGER.info("Response -> {}", response);


        // Assert: Validate response status
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().size() >= 1, "Response should have at least one order");

        // Convert Set to List to access elements by index
        List<OrdersDto> ordersList = new ArrayList<>(response.getBody());

        // Extract first two orders
        OrdersDto firstOrder = ordersList.get(0);
//        OrdersDto secondOrder = ordersList.get(1);

        // Assert details of the first two orders
        Assertions.assertNotNull(firstOrder.getName());
        Assertions.assertNotNull(firstOrder.getOrderId());
        Assertions.assertTrue(firstOrder.getQty() >= 0);
        Assertions.assertTrue(firstOrder.getPrice() >= 0);

        orderId = firstOrder.getOrderId();

    }

    @Test
    @Order(6)
    public void testUpdateOrder_whenValidRequest_returns200() {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setOrderId(orderId);
        orderDto.setName("Rolands Bidzans");
        orderDto.setQty(10);
        orderDto.setPrice(570);
        orderDto.setStatus("Updated");
        orderDto.setAccountNumber(validAccountNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        //Use HttpEntity with the correct body (orderDto)
        HttpEntity<OrdersDto> requestEntity = new HttpEntity<>(orderDto, headers);

        // Act
        ResponseEntity<ResponseDto>  response = testRestTemplate.exchange(
                "/api/orders/update",
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<ResponseDto>() {}
        );

//        LOGGER.info("Response -> {}", response);


        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("200", response.getBody().getStatusCode());
        Assertions.assertEquals("Order updated successfully", response.getBody().getStatusMsg());

    }


    @Test
    @Order(7)
    public void fetchUpdatedOrder_whenValidRequest_returns200() {
        // Arrange

        // Act
        ResponseEntity<OrdersDto> response = testRestTemplate.exchange(
                "/api/orders/fetch?orderId=" + orderId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OrdersDto>() {}
        );

//        LOGGER.info("Response -> {}", response);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(orderId, response.getBody().getOrderId());
        Assertions.assertEquals("Rolands Bidzans", response.getBody().getName());
        Assertions.assertEquals(10, response.getBody().getQty());
        Assertions.assertEquals(570, response.getBody().getPrice());
        Assertions.assertEquals("Updated", response.getBody().getStatus());
        Assertions.assertEquals("123456789", response.getBody().getAccountNumber());

    }

    @Test
    @Order(8)
    public void deleteOrder_whenValidOrderIdProvided_returns404() {
        // Arrange

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/orders/delete?orderId=" + orderId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<ResponseDto>() {}
        );

//        LOGGER.info("Response -> {}", response);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("200", response.getBody().getStatusCode());
        Assertions.assertEquals("Request processed successfully", response.getBody().getStatusMsg());

    }

    @Test
    @Order(9)
    public void deleteOrder_whenInvalidOrderIdProvided_returns404() {

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/orders/delete?orderId=" + orderId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<ResponseDto>() {}
        );

        //LOGGER.info("Response -> {}", response);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
        Assertions.assertEquals("Order to delete not found.", response.getBody().getStatusMsg());

    }

    @Test
    @Order(10)
    public void fetchDeletedOrder_whenInvalidOrderIdProvided_returns40() {
        // Arrange

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/orders/fetch?orderId=" + orderId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ResponseDto>() {}
        );

        LOGGER.info("Response -> {}", response);

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
        Assertions.assertEquals("Order not found for the given ID.", response.getBody().getStatusMsg());

    }
}

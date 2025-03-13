package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.OrdersConstants;
import com.eazybytes.accounts.dto.NotificationEvent;
import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.publisher.NotificationProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping(path="/api/orders", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private NotificationProducer notificationProducer;

    @Autowired
    WebClient webClient;

    @Autowired
    public OrderController(NotificationProducer orderProducer) {
        this.notificationProducer = orderProducer;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> placeOrder(@RequestParam String receiverEmail,
                                                  @Valid @RequestBody OrdersDto orderDto) {
        try {
            orderDto.setOrderId(UUID.randomUUID().toString());

            String uri = "http://localhost:8083/api/orders/create";

            webClient.post().uri(uri)
                    .bodyValue(orderDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            NotificationEvent event = new NotificationEvent();
            event.setReceiverEmail(receiverEmail);
            event.setMessage("Order status is in PLACED state");
            event.setOrder(orderDto);

            notificationProducer.sendMessage(event);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(OrdersConstants.STATUS_201, OrdersConstants.MESSAGE_201));

        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDto(OrdersConstants.STATUS_400, OrdersConstants.MESSAGE_400_CREATE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE));
        }
    }



    @GetMapping("/fetchAll")
    public ResponseEntity<?> fetchAllOrdersDetails(@RequestParam
                                                                String accountNumber) {
        try {
            String uri = "http://localhost:8083/api/orders/fetchAll?accountNumber=" + accountNumber;

            // Proper WebClient call for GET request returning a Set<OrdersDto>
            Set<OrdersDto> ordersDto = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Set<OrdersDto>>() {})
                    .block()
                    .getBody();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ordersDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_FETCH_ALL));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH_ALL));
        }
        catch (Exception e) {  // Catch any other unexpected errors

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH_ALL));
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchOrdersDetails(@RequestParam
                                                        String orderId) {

        try {
            String uri = "http://localhost:8083/api/orders/fetch?orderId=" + orderId;

            OrdersDto ordersDto = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(OrdersDto.class)
                    .block()
                    .getBody();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ordersDto);

        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_FETCH));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateOrderDetails(@Valid @RequestBody OrdersDto orderDto) {


        try {
            String uri = "http://localhost:8083/api/orders/update";

            ResponseDto responseDto = webClient.put()
                    .uri(uri)
                    .bodyValue(orderDto)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ResponseDto>() {})
                    .block()
                    .getBody();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_UPDATE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_UPDATE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteOrderDetails(@RequestParam
                                                          String orderId) {
        try {
            String uri = "http://localhost:8083/api/orders/delete?orderId=" + orderId;

            ResponseDto responseDto = webClient.delete()
                    .uri(uri)
                    .retrieve()
                    .toEntity(ResponseDto.class)  // Expecting a ResponseDto as response
                    .block()
                    .getBody();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_DELETE));
            } else if (e.getStatusCode() == HttpStatus.EXPECTATION_FAILED) {
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new ResponseDto(OrdersConstants.STATUS_417, OrdersConstants.MESSAGE_417_DELETE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_DELETE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_DELETE));
        }
    }

}

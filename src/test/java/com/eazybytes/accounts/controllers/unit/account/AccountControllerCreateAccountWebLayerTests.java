package com.eazybytes.accounts.controllers.unit.account;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.controller.AccountsController;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.service.IAccountsService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// This allows to run tests in parallel
// in aplication.properties we have server.port=0 -> means random
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerCreateAccountWebLayerTests {
    private Logger LOGGER = LoggerFactory.getLogger(AccountControllerCreateAccountWebLayerTests.class);

    @Autowired
    private AccountsController accountsController;

    @Mock
    private IAccountsService iAccountsService;


    @Test
    @DisplayName("Create Valid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testCreateAccount_whenValidDetailsProvided_returnsCorrectStatusCode() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("John Doe");
        accountDto.setEmail("johndoe@example.com");
        accountDto.setMobileNumber("12345678");

        // Mock the IAccountsService.createAccount method
        Mockito.doNothing().when(iAccountsService).createAccount(Mockito.any(AccountsDto.class));

        // Call the createAccount method of the AccountsController
        ResponseEntity<ResponseDto> response = accountsController.createAccount(accountDto);

        // Verify the response
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(AccountsConstants.STATUS_201, response.getBody().getStatusCode());
        Assertions.assertEquals(AccountsConstants.MESSAGE_201, response.getBody().getStatusMsg());
    }

    @Test
    @DisplayName("Create Invalid Account With Empty Name: Check ConstraintViolationException and Json Error Message")
    public void testCreateAccount_whenEmptyNameDetailsProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("");
        accountDto.setEmail("johndoe3@example.com");
        accountDto.setMobileNumber("12345678");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Name can not be a null or empty";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);
    }

    @Test
    @DisplayName("Create Invalid Account With Null Name: Check ConstraintViolationException and Json Error Message")
    public void testCreateAccount_whenNullNameProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName(null);
        accountDto.setEmail("johndoe3@example.com");
        accountDto.setMobileNumber("12345678");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Name can not be a null or empty"; // or the actual message in your case
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);

    }

    @Test
    @DisplayName("Create Invalid Account With Too Short Name: Check ConstraintViolationException and Json Error Message")
    public void testCreateAccount_whenShortNameProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("Rol");
        accountDto.setEmail("johndoe3@example.com");
        accountDto.setMobileNumber("12345678");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Name must be longer than 4 letters and contain only letters and spaces";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);
    }

    @Test
    public void testCreateAccount_whenInvalidEmailProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("Rolnds Bidzans");
        accountDto.setEmail("johndoe3");
        accountDto.setMobileNumber("12345678");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Invalid email format";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);

    }

    @Test
    public void testCreateAccount_whenTooShortInvalidPhoneNumberProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("Rolnds Bidzans");
        accountDto.setEmail("johndoe3@inbox.lv");
        accountDto.setMobileNumber("12");

        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Mobile number must be 8 digits";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);
    }

    @Test
    public void testCreateAccount_whenTooLongInvalidPhoneNumberProvided_returnsConstraintViolationException() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("Rolnds Bidzans");
        accountDto.setEmail("johndoe3");
        accountDto.setMobileNumber("123456789");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessage = "Mobile number must be 8 digits";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessage), "Expected error message not found: " + expectedErrorMessage);
    }

    @Test
    public void testCreateAccount_whenAllInvalidFieldsProvided_returnsConstraintViolationExceptionWithJsonMessage() throws Exception {
        // Create a mock AccountsDto object
        AccountsDto accountDto = new AccountsDto();
        accountDto.setName("Rol");
        accountDto.setEmail("johndoe3");
        accountDto.setMobileNumber("1234567893232");


        // Assert & Act
        ConstraintViolationException thrown = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountsController.createAccount(accountDto);
        }, "Was expecting ConstraintViolationException to be thrown");

        // Assert
        String expectedErrorMessageMobile = "Mobile number must be 8 digits";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessageMobile), "Expected error message not found: " + expectedErrorMessageMobile);

        String expectedErrorMessageEmail = "Name must be longer than 4 letters and contain only letters and spaces";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessageEmail), "Expected error message not found: " + expectedErrorMessageEmail);

        String expectedErrorMessageName = "Invalid email format";
        Assertions.assertTrue(thrown.getMessage().contains(expectedErrorMessageName), "Expected error message not found: " + expectedErrorMessageName);
    }
}

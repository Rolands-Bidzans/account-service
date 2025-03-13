package com.eazybytes.accounts.repositories;

import com.eazybytes.accounts.entities.Accounts;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountRepositoryTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AccountsRepository accountsRepository;

    private String userId1 = UUID.randomUUID().toString();
    private String email1 = "RB@example.com";

    private String userId2 = UUID.randomUUID().toString();
    private String email2 = "Ak@example.com";


    @BeforeEach
    @Transactional
    void setup() {
        // Arrange
        Accounts user1 = new Accounts();
        user1.setAccountNumber(userId1);
        user1.setEmail(email1);
        user1.setName("Rolands Bidzans");
        user1.setMobileNumber("12345678");

        entityManager.persist(user1);
        entityManager.flush();

        Accounts user2 = new Accounts();
        user2.setAccountNumber(userId2);
        user2.setEmail(email2);
        user2.setName("Andrejs Petrovs");
        user2.setMobileNumber("442343243");

        entityManager.persist(user2);
        entityManager.flush();

    }

    @Test
    @Transactional
    @Order(1)
    void testFindByEmail_whenGivenCorrectEmail_returnsUserEntity() {

        // Act
        Optional<Accounts> storedUser = accountsRepository.findByEmail(email1);

        // Assert
        Assertions.assertEquals(email1, storedUser.get().getEmail(),
                () -> "The  returned user email does not match the expected value");

    }

    @Test
    @Transactional
    @Order(2)
    void testDeleteByAccountNumber_whenCorrectAccountNumberProvided_returnsSuccess() {

        // Act: Delete by accountNumber
        accountsRepository.deleteByAccountNumber(userId1);

        // Assert: Verify it's deleted
        Optional<Accounts> deletedAccount = accountsRepository.findByEmail(email1);
        Assertions.assertTrue(deletedAccount.isEmpty(), "Account should be deleted");

    }

}

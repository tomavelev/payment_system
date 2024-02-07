package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.TransactionStatus;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.UserRepository;
import com.tomavelev.payment.service.JwtTokenProvider;
import com.tomavelev.payment.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;

import static com.tomavelev.payment.rest.TestAuthController.initUserVariations;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestTransactionRestController {

//    @Autowired
//    private TransactionService transactionService;

    @Autowired
    protected TestRestTemplate template;
    private String token;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private TransactionTemplate transactionTemplate;


    @Before
    @Transactional
    public void init() throws Exception {
        transactionService.cleanTransactionsOlderThan(new Date());
        userRepository.deleteAll();

        initUserVariations(passwordEncoder, userRepository);
        token = tokenProvider.generateToken("testma@test.test", new ArrayList<>(List.of(User.ROLE_MERCHANT)));
    }

    @After
    @Transactional
    public void destroy() {
        transactionService.cleanTransactionsOlderThan(new Date());
        userRepository.deleteAll();
    }

    @Test
    public void testGetTransactions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<Void> requestEntityTransaction = new HttpEntity<>(null, headers);
        ResponseEntity<RestResponse<User>> response = template.exchange(template.getRootUri()
                                                                        + "/transactions",
                HttpMethod.GET, requestEntityTransaction, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertTrue(Objects.requireNonNull(response.getBody()).list().isEmpty());

        testSaveTransaction();

        response = template.exchange(template.getRootUri()
                                     + "/transactions",
                HttpMethod.GET, requestEntityTransaction, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertFalse(Objects.requireNonNull(response.getBody()).list().isEmpty());

        for (int i = 0; i < 11; i++) {
            testSaveTransaction();
        }

        response = template.exchange(template.getRootUri()
                                     + "/transactions?offset=10",
                HttpMethod.GET, requestEntityTransaction, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertFalse(Objects.requireNonNull(response.getBody()).list().isEmpty());
    }

    @Test
    public void testSaveTransaction() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setAmount(new BigDecimal("0.01"));
        transaction.setCustomerEmail("testma2@test.test");
        transaction.setCustomerPhone("+359882626454");
        transaction.setStatus(TransactionStatus.APPROVED);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<PaymentTransaction> requestEntityTransaction = new HttpEntity<>(transaction, headers);
        ResponseEntity<PaymentResponse> response = template.exchange(template.getRootUri()
                                                                     + "/transactions",
                HttpMethod.POST, requestEntityTransaction, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertSame(Objects.requireNonNull(response.getBody()).code(), BusinessCode.SUCCESS);
//        test not empty
    }


}

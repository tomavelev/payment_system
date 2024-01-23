package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.LoginResponse;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestAuthController {

    @Autowired
    protected TestRestTemplate template;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private static boolean setUpIsDone = false;

    @Before
    @Transactional
    public void init() throws Exception {
        if (setUpIsDone) {
            return;
        }
        // do the setup
        setUpIsDone = true;
        initUserVariations(passwordEncoder, userRepository);
    }


    public static void initUserVariations(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        User user = new User();
        user.setEmail("test@test.test");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        user = new User();
        user.setEmail("testm@test.test");
        user.setPassword(passwordEncoder.encode("password"));
        Merchant merchant = new Merchant();
        merchant.setDescription("description");
        merchant.setName("merchant ");
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setActive(Boolean.FALSE);
        user.setMerchant(merchant);

        userRepository.save(user);

        user = new User();
        user.setEmail("testma@test.test");
        user.setPassword(passwordEncoder.encode("password"));
        merchant = new Merchant();
        merchant.setDescription("description");
        merchant.setName("merchant ");
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setActive(Boolean.TRUE);
        user.setMerchant(merchant);

        userRepository.save(user);

    }

    @Test
    public void testSuccessAuthenticateUser() {
        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=testma@test.test");
        sb.append("&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        assertTrue(result.hasBody());
        assertNotNull(Objects.requireNonNull(result.getBody()).token());
    }

    @Test
    public void testWrongPasswordAuthenticateUser() {
        //TODO runs successfully standalone - fails then run as part of the full project test
        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=testma@test.test");
        sb.append("&password=password2");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        assertTrue(result.hasBody());
        assertNotNull(Objects.requireNonNull(result.getBody()).code());
        assertEquals(result.getBody().code(), BusinessCode.WRONG_PASSWORD);

    }

    @Test
    public void testNoProfileAuthenticateUser() {

        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=tes2tma@test.test");
        sb.append("&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        assertTrue(result.hasBody());
        assertNotNull(Objects.requireNonNull(result.getBody()).code());
        assertEquals(result.getBody().code(), BusinessCode.PROFILE_NOT_FOUND);
    }
}
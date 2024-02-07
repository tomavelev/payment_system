package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.LoginResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.TransactionRepository;
import com.tomavelev.payment.repository.UserRepository;
import com.tomavelev.payment.service.JwtTokenProvider;
import com.tomavelev.payment.service.UserService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tomavelev.payment.rest.TestAuthController.initUserVariations;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestUserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    @Transactional
    public void init() {
        userRepository.deleteAll();
        initUserVariations(passwordEncoder, userRepository);
    }

    @After
    @Transactional
    public void destroy() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Autowired
    protected TestRestTemplate template;


    @Test
    public void update() {
        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=test@test.test&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        assertTrue(result.hasBody());
        assertNotNull(Objects.requireNonNull(result.getBody()).token());


        User user = userRepository.findByEmail("test@test.test");
        user.setPassword(passwordEncoder.encode("123123123"));
        headers.setBearerAuth(result.getBody().token());
        HttpEntity<User> requestEntityUser = new HttpEntity<>(user, headers);

        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> response = template.exchange(template.getRootUri() + "/admin/user",
                HttpMethod.PUT, requestEntityUser, Void.class);
        assertSame(response.getStatusCode(), HttpStatus.OK);

        user = userRepository.findByEmail("test@test.test");
        passwordEncoder.matches("123123123", user.getPassword());
    }



    @Test
    public void users() {

        String token = tokenProvider.generateToken("test@test.test", new ArrayList<>(List.of(User.ROLE_ADMIN)));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntityUser = new HttpEntity<>(null, headers);

        ResponseEntity<RestResponse<User>> response = template.exchange(template.getRootUri()
                                                                        + "/admin/user",
                HttpMethod.GET, requestEntityUser, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertFalse(Objects.requireNonNull(response.getBody()).list().isEmpty());

        String password = passwordEncoder.encode("password");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setEmail("test@test.test" + i);
            user.setPassword(password);

            users.add(user);
        }
        userRepository.saveAllAndFlush(users);

        response = template.exchange(template.getRootUri()
                                     + "/admin/user?offset=10",
                HttpMethod.GET, requestEntityUser, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);
        assertFalse(Objects.requireNonNull(response.getBody()).list().isEmpty());
    }
}

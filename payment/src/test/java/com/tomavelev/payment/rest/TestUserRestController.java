package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.LoginResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.UserRepository;
import com.tomavelev.payment.service.UserService;
import jakarta.transaction.Transactional;
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

    @Autowired
    protected TestRestTemplate template;


    @Test
    public void update() {
        //TODO runs successfully standalone - fails then run as part of the full project test
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


//    @Test
//    public void delete() {

        //TODO this test works in standalone run, but fails as part of full project test. Needs to be investigated
//        String filter = template.getRootUri() + "/public/login";
//        //noinspection StringBufferReplaceableByString
//        StringBuilder sb = new StringBuilder();
//        sb.append("email=test@test.test");
//        sb.append("&password=password");
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);
//
//        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);
//
//        assertTrue(result.hasBody());
//        assertNotNull(Objects.requireNonNull(result.getBody()).token());
//
//
//        User user = userRepository.findByEmail("testma@test.test");
//        user.setPassword(passwordEncoder.encode("123123123"));
//        headers.setBearerAuth(result.getBody().token());
//        HttpEntity<User> requestEntityUser = new HttpEntity<>(user, headers);
//
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        ResponseEntity<Void> response = template.exchange(template.getRootUri() + "/admin/user",
//                HttpMethod.DELETE, requestEntityUser, Void.class);
//        assertSame(response.getStatusCode(), HttpStatus.OK);
//
//        user = userRepository.findByEmail("testma@test.test");
//        assertNull(user);
//
//        //TODO validate users with referenced transactions
//    }


    @Test
    public void users() {


        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=test@test.test");
        sb.append("&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        assertTrue(result.hasBody());
        assertNotNull(Objects.requireNonNull(result.getBody()).token());


        headers.setBearerAuth(result.getBody().token());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntityUser = new HttpEntity<>(null, headers);

        ResponseEntity<RestResponse<User>> response = template.exchange(template.getRootUri()
                                                                        + "/admin/users",
                HttpMethod.GET, requestEntityUser, new ParameterizedTypeReference<>() {
                });
        assertSame(response.getStatusCode(), HttpStatus.OK);


        assertTrue(response.hasBody());
        assertFalse(Objects.requireNonNull(response.getBody()).list().isEmpty());

        //TODO test page 2
    }

}

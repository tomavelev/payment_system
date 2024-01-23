package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.response.LoginResponse;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.Assert;
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

import java.util.Objects;

import static com.tomavelev.payment.rest.TestAuthController.initUserVariations;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestSampleController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    protected TestRestTemplate template;
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


    @Test
    public void publicHello() {

        String filter = template.getRootUri() + "/public/hello";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> result = template.exchange(filter, HttpMethod.GET, requestEntity, String.class);

        Assert.assertTrue(result.hasBody());
        Assert.assertEquals("Hello from public endpoint!", result.getBody());
    }

    @Test
    public void userHello() {

        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=testm@test.test");
        sb.append("&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        Assert.assertTrue(result.hasBody());
        Assert.assertNotNull(Objects.requireNonNull(result.getBody()).token());

        filter = template.getRootUri() + "/user/hello";
        //noinspection StringBufferReplaceableByString
        sb = new StringBuilder();
        headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(result.getBody().token());
        requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> result1 = template.exchange(filter, HttpMethod.GET, requestEntity, String.class);

        Assert.assertTrue(result1.hasBody());
        String userString = "Hello from user endpoint!";
        Assert.assertEquals(userString, result1.getBody());
    }

    @Test
    public void adminHello() {
        String filter = template.getRootUri() + "/public/login";
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("email=test@test.test");
        sb.append("&password=password");
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<LoginResponse> result = template.exchange(filter, HttpMethod.POST, requestEntity, LoginResponse.class);

        Assert.assertTrue(result.hasBody());
        Assert.assertNotNull(Objects.requireNonNull(result.getBody()).token());

        filter = template.getRootUri() + "/admin/hello";
        //noinspection StringBufferReplaceableByString
        sb = new StringBuilder();
        headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(result.getBody().token());
        requestEntity = new HttpEntity<>(sb.toString(), headers);

        ResponseEntity<String> result1 = template.exchange(filter, HttpMethod.GET, requestEntity, String.class);

        Assert.assertEquals(true, result1.hasBody());
        String userString = "Hello from admin endpoint!";
    }
}

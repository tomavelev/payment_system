package com.tomavelev.payment.service;

import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.repository.TransactionRepository;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import static com.tomavelev.payment.rest.TestAuthController.initUserVariations;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Before
    @Transactional
    public void init() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        initUserVariations(passwordEncoder, userRepository);
    }

    @After
    @Transactional
    public void destroy() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testImportFromSCV() throws IOException, CsvChainedException, CsvFieldAssignmentException {
        File file = File.createTempFile("testImport", "");
        String text = """
                "email","password","name","description","active","total_transaction_sum"
                "testmerchant@test.testimport","$2a$12$uEEhlvNbsZc770vNwxv9zukPY1PI3Q4H68mzxiHQRgmSAhTwqof8W","merchant 7","description 7","false","7"                
                "testadmin@test.testimport","$2a$12$UV9/SOcfiRRxZyMB/cTWFu55I44wn9ulMGEP/ogJiwo9dWbe.7bUK","","","",""
                """;
        Files.writeString(file.toPath(), text, StandardOpenOption.TRUNCATE_EXISTING);
        userService.importFromSCV(file);

        User user = userService.findByEmail("testmerchant@test.testimport");
        assertNotNull(user);

        user = userService.findByEmail("testadmin@test.testimport");
        assertNotNull(user);

        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    @Test
    public void testGenerateSCV() throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        File file = File.createTempFile("testImport", "");

        userService.generateSCV(file.getAbsolutePath());

        assertTrue(file.length() > 0);
        //noinspection ResultOfMethodCallIgnored
        file.delete();

    }


    @Test
    public void delete() {

        User user = userRepository.findByEmail("testma@test.test");
        userService.delete(user);
        user = userRepository.findByEmail("testma@test.test");

        assertNull(user);

        String password = passwordEncoder.encode("password");
        user = new User();
        user.setEmail("testma@test.test");
        user.setPassword(password);
        Merchant merchant = new Merchant();
        merchant.setDescription("description");
        merchant.setName("merchant ");
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setActive(Boolean.TRUE);
        user.setMerchant(merchant);

        userService.update(user);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setCustomerEmail("test@test.test");
        transaction.setAmount(BigDecimal.ONE);
        transaction.setCustomerPhone("+359882626454");
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setMerchant(merchant);

        transactionRepository.saveAndFlush(transaction);
//        Ensure you prevent a merchant from being deleted unless there are no
//        related payment transactions
        try {
            userService.delete(user);
            throw new RuntimeException("Deleting merchant with Transactions has passed without Exception and it should not happen");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Cannot delete Merchant with Transactions");
        }
    }

}

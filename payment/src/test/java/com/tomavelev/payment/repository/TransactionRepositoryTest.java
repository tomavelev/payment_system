package com.tomavelev.payment.repository;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.util.Check;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;


    @Test
    public void selectAll() throws Exception {

        List<PaymentTransaction> result = transactionRepository.findAll();
        assertNotNull(result);
        result = transactionRepository.findAll(Sort.by("status"));
        assertNotNull(result);
    }

    @Test
    @Transactional
    public void insert() throws Exception {
        PaymentTransaction transaction = newPaymentTransaction();
        transaction = transactionRepository.save(transaction);
        assertNotNull(transaction.getId());
    }

    @Test
    @Transactional
    public void update() throws Exception {
        PaymentTransaction transaction = newPaymentTransaction();
        transaction = transactionRepository.save(transaction);
        assertNotNull(transaction.getId());
        transaction.setAmount(BigDecimal.ONE);
        transactionRepository.save(transaction);

        transaction = transactionRepository.findById(transaction.getId()).orElse(null);

        assertNotNull(transaction);
        assertEquals(BigDecimal.ONE, transaction.getAmount());
    }

    @Test
    @Transactional
    public void delete() throws Exception {
        PaymentTransaction transaction = newPaymentTransaction();
        transaction = transactionRepository.save(transaction);
        assertNotNull(transaction.getId());
        transactionRepository.delete(transaction);
        Optional<PaymentTransaction> merchant1 = transactionRepository.findById(transaction.getId());
        assertFalse(merchant1.isPresent());
    }

//    @Test //TODO test deleteAllByCreatedAtLessThan
//    @Transactional
//    public void deleteAllByCreatedAtLessThan() throws Exception {
//        PaymentTransaction transaction = newPaymentTransaction();
//        transaction.setCreatedAt(new Date());
//        transaction = transactionRepository.save(transaction);
//
//        transaction = newPaymentTransaction();
//        transaction = transactionRepository.save(transaction);
//        transaction.setCreatedAt(new Date(System.currentTimeMillis() - (Check.HOUR + 1)));
//        transaction = transactionRepository.save(transaction);
//
//        List<PaymentTransaction> result = transactionRepository.findAll();
//        assertEquals(2, result.size());
//
//        transactionRepository.deleteAllByCreatedAtLessThan(new Date(System.currentTimeMillis() - Check.HOUR));
//        result = transactionRepository.findAll();
//        assertEquals(1, result.size());
//
//    }

    @Test
    @Transactional
    public void loadById() throws Exception {
        PaymentTransaction transaction = newPaymentTransaction();
        transaction = transactionRepository.save(transaction);
        assertNotNull(transaction.getId());
        Optional<PaymentTransaction> merchant1 = transactionRepository.findById(transaction.getId());
        assertTrue(merchant1.isPresent());
    }

    private PaymentTransaction newPaymentTransaction() {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setCustomerEmail("test@test.test");
        transaction.setUuid(UUID.randomUUID().toString());
        return transaction;
    }
}

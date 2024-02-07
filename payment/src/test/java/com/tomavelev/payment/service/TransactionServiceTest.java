package com.tomavelev.payment.service;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.TransactionStatus;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.repository.MerchantRepository;
import com.tomavelev.payment.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    TransactionService transactionService;

    @Test
    public void testSaveTransactionReferencesShort() {
//        Authorize Transaction -> Reversal  Transaction
        User user = new User();
        user.setEmail("testma1@test.test");
        user.setPassword("123");//not important at the moment
        Merchant merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        user = new User();
        user.setEmail("testma2@test.test");
        user.setPassword("123");//not important at the moment
        merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setAmount(new BigDecimal("0.05"));
        paymentTransaction.setStatus(TransactionStatus.APPROVED);
        paymentTransaction.setUuid(UUID.randomUUID().toString());
        paymentTransaction.setCustomerEmail("testma1@test.test");
        paymentTransaction.setCustomerPhone("+359882626454");

        transactionService.save(paymentTransaction,"testma2@test.test");

        String approvedId = paymentTransaction.getId();

        user = userRepository.findByEmail("testma1@test.test");

        Assert.assertEquals(new BigDecimal("0.95000"), user.getMerchant().getTotalTransactionSum());

        paymentTransaction = new PaymentTransaction();
        paymentTransaction.setStatus(TransactionStatus.REVERSED);
        paymentTransaction.setReferenceId(approvedId);
        paymentTransaction.setUuid(UUID.randomUUID().toString());

        transactionService.save(paymentTransaction, "testma2@test.test");

        user = userRepository.findByEmail("testma1@test.test");
        Assert.assertEquals(new BigDecimal("1.00000"), user.getMerchant().getTotalTransactionSum());
    }

    @Test
    public void testSaveTransactionReferencesLong() {
//        Authorize Transaction -> Charge Transaction -> Refund Transaction
        User user = new User();
        user.setEmail("testma1@test.test");
        user.setPassword("123");//not important at the moment
        Merchant merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        user = new User();
        user.setEmail("testma2@test.test");
        user.setPassword("123");//not important at the moment
        merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setAmount(new BigDecimal("0.05"));
        paymentTransaction.setStatus(TransactionStatus.APPROVED);
        paymentTransaction.setUuid(UUID.randomUUID().toString());
        paymentTransaction.setCustomerEmail("testma1@test.test");
        paymentTransaction.setCustomerPhone("+359882626454");

        transactionService.save(paymentTransaction,"testma2@test.test");

        String approvedId = paymentTransaction.getId();

        user = userRepository.findByEmail("testma1@test.test");

        Assert.assertEquals(new BigDecimal("0.95000"), user.getMerchant().getTotalTransactionSum());

        paymentTransaction = new PaymentTransaction();
        paymentTransaction.setStatus(TransactionStatus.CHARGE);
        paymentTransaction.setReferenceId(approvedId);
        paymentTransaction.setUuid(UUID.randomUUID().toString());

        transactionService.save(paymentTransaction, "testma2@test.test");

        user = userRepository.findByEmail("testma1@test.test");
        Assert.assertEquals(new BigDecimal("0.95000"), user.getMerchant().getTotalTransactionSum());

        user = userRepository.findByEmail("testma2@test.test");
        Assert.assertEquals(new BigDecimal("1.05000"), user.getMerchant().getTotalTransactionSum());


        String chargeId = paymentTransaction.getId();

        paymentTransaction = new PaymentTransaction();
        paymentTransaction.setStatus(TransactionStatus.REFUNDED);
        paymentTransaction.setReferenceId(chargeId);
        paymentTransaction.setUuid(UUID.randomUUID().toString());

        transactionService.save(paymentTransaction, "testma2@test.test");

        user = userRepository.findByEmail("testma1@test.test");
        Assert.assertEquals(new BigDecimal("1.00000"), user.getMerchant().getTotalTransactionSum());

        user = userRepository.findByEmail("testma2@test.test");
        Assert.assertEquals(new BigDecimal("1.00000"), user.getMerchant().getTotalTransactionSum());
    }

    @Test
    public void testSaveTransactionValidations() {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        PaymentResponse save = transactionService.save(paymentTransaction, "test@test.test");
        Assert.assertSame(save.code(), BusinessCode.PROFILE_NOT_FOUND);


        paymentTransaction = new PaymentTransaction();
        User user = new User();
        user.setEmail("testma2@test.test");
        user.setPassword("123");//not important at the moment
        Merchant merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        user = new User();
        user.setEmail("test@test.test");
        user.setPassword("123");//not important at the moment
        merchant = new Merchant();
        merchant.setActive(true);
        merchant.setTotalTransactionSum(BigDecimal.ONE);
        merchant.setName("test");
        merchant.setDescription("test");
        user.setMerchant(merchant);
        userRepository.save(user);

        //test save Transaction with no data
        save = transactionService.save(paymentTransaction, "test@test.test");
        Assert.assertSame(save.code(), BusinessCode.ERROR);

        //test inactive merchant
        merchant.setActive(false);
        merchantRepository.save(merchant);

        paymentTransaction.setAmount(new BigDecimal("0.05"));
        paymentTransaction.setStatus(TransactionStatus.APPROVED);
        paymentTransaction.setUuid(UUID.randomUUID().toString());
        paymentTransaction.setCustomerEmail("testma2@test.test");
        paymentTransaction.setCustomerPhone("+359882626454");
        paymentTransaction.setMerchant(merchant);

        save = transactionService.save(paymentTransaction, "test@test.test");
        Assert.assertSame(save.code(), BusinessCode.MERCHANT_NOT_ACTIVE);

        merchant.setActive(true);
        merchantRepository.save(merchant);

        //test invalid customer uuid
        paymentTransaction.setUuid("");

        save = transactionService.save(paymentTransaction, "test@test.test");
        Assert.assertSame(save.code(), BusinessCode.ERROR);
        Assert.assertEquals("uuid value: '' must not be blank\n", save.message());

        paymentTransaction.setUuid(UUID.randomUUID().toString());
        //test invalid amount
        paymentTransaction.setAmount(BigDecimal.ZERO);

        save = transactionService.save(paymentTransaction, "test@test.test");

        Assert.assertSame(save.code(), BusinessCode.ERROR);
        Assert.assertEquals("amount value: '0' must be greater than 0.0\n", save.message());

        paymentTransaction.setAmount(new BigDecimal("0.05"));
        //test invalid customer phone

        paymentTransaction.setCustomerPhone("123asd123");
        save = transactionService.save(paymentTransaction, "test@test.test");

        Assert.assertSame(save.code(), BusinessCode.ERROR);
        Assert.assertEquals("customerPhone value: '123asd123' Please enter a valid phone number\n", save.message());

        paymentTransaction.setCustomerPhone("+359882626454");
        //test invalid customer email

        paymentTransaction.setCustomerEmail("asdasdasd");
        save = transactionService.save(paymentTransaction, "test@test.test");

        Assert.assertSame(save.code(), BusinessCode.ERROR);
        Assert.assertEquals("customerEmail value: 'asdasdasd' must be a well-formed email address\n", save.message());


        paymentTransaction.setCustomerEmail("test@test.test");
        save = transactionService.save(paymentTransaction, "test@test.test");
        Assert.assertSame(save.code(), BusinessCode.SUCCESS);
    }
}
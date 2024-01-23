package com.tomavelev.payment.rest;

import com.tomavelev.payment.PaymentApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestTransactionRestController {

//    @Autowired
//    private TransactionService transactionService;


    @Test
    public void acceptPayment() {
        //TODO acceptPayment
        //    @PostMapping("/user/acceptPayment")/ with @RequestBody PaymentTransaction transaction
//        return transactionService.save(transaction);
    }

    @Test
    public void transactions() {
        //TODO transactions
        //RestResponse<PaymentTransaction>
//        @GetMapping(value = "/user/transactions")
//        @RequestParam(value = "offset", defaultValue = "0") long offset,
//        @RequestParam(value = "limit", defaultValue = "10") int limit
//        return transactionService.getTransactions(offset, limit);
    }

}

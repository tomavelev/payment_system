package com.tomavelev.payment.repository;

import com.tomavelev.payment.PaymentApplication;
import com.tomavelev.payment.model.Merchant;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MerchantRepositoryTest {

    @Autowired
    private MerchantRepository merchantRepository;


    @Test
    public void selectAll() throws Exception {

        List<Merchant> result = merchantRepository.findAll();
        assertNotNull(result);
        result = merchantRepository.findAll(Sort.by("active"));
        assertNotNull(result);
    }

    @Test
    @Transactional
    public void insert() throws Exception {
        Merchant merchant = new Merchant();
        merchant = merchantRepository.save(merchant);
        assertNotNull(merchant.getId());
    }

    @Test
    @Transactional
    public void update() throws Exception {
        Merchant merchant = new Merchant();
        merchant = merchantRepository.save(merchant);
        assertNotNull(merchant.getId());
        merchant.setName("test");
        merchantRepository.save(merchant);

        merchant = merchantRepository.findById(merchant.getId()).orElse(null);

        assertNotNull(merchant);
        assertEquals("test", merchant.getName());
    }

    @Test
    @Transactional
    public void delete() throws Exception {
        Merchant merchant = new Merchant();
        merchant = merchantRepository.save(merchant);
        assertNotNull(merchant.getId());
        merchantRepository.delete(merchant);
        Optional<Merchant> merchant1 = merchantRepository.findById(merchant.getId());
        assertFalse(merchant1.isPresent());
    }

    @Test
    @Transactional
    public void loadById() throws Exception {
        Merchant merchant = new Merchant();
        merchant = merchantRepository.save(merchant);
        assertNotNull(merchant.getId());
        Optional<Merchant> merchant1 = merchantRepository.findById(merchant.getId());
        assertTrue(merchant1.isPresent());
    }
}

package com.tomavelev.payment.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.UserRepository;
import com.tomavelev.payment.util.UserCsvMappingStrategy;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RestResponse<User> getUsers(long offset, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        Page<User> page = userRepository.findAll(PageRequest.ofSize(limit).withPage((int) (offset / limit)));
        return new RestResponse<>(page.get().toList(), page.getTotalElements(), null, BusinessCode.SUCCESS);
    }

    @Transactional
    public void importFromSCV(String file) throws IOException, CsvFieldAssignmentException, CsvChainedException {

        final List<User> users = new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                csvReader.skip(1);

                for (String[] line : csvReader) {
                    users.add(UserCsvMappingStrategy.instance().populateNewBean(line));
                }

                //maybe if csv file is large this could be batched to the db in chunks
//                csvToBean.forEach(users::add);
                userRepository.saveAll(users);
            }
        }
    }

    public void generateSCV(String filePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        File file = new File(filePath);
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        try (Writer writer = new FileWriter(file)) {
            CSVWriter csvWriter = new CSVWriter(writer);
            StatefulBeanToCsv<User> build = new StatefulBeanToCsvBuilder<User>(csvWriter)
                    .withMappingStrategy(UserCsvMappingStrategy.instance()).build();
            Random random = new Random();
            User user;
            Merchant merchant;
            for (int i = 0; i < 1000; i++) {

                user = new User();
                user.setEmail(i + "test@test.test");
                //should be random, but for demo purposes - let it be known
                user.setPassword(passwordEncoder.encode("password" + i));

                if (random.nextBoolean()) {
                    merchant = new Merchant();
                    merchant.setDescription("description " + i);
                    merchant.setName("merchant " + i);
                    merchant.setTotalTransactionSum(BigDecimal.valueOf(i));
                    merchant.setActive(random.nextBoolean());
                    user.setMerchant(merchant);
                }

                build.write(user);
            }
        }
    }
}

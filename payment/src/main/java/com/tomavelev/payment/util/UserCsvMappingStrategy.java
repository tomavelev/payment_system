package com.tomavelev.payment.util;

import com.opencsv.CSVReader;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.User;

import java.io.IOException;
import java.math.BigDecimal;

public class UserCsvMappingStrategy extends ColumnPositionMappingStrategy<User> {

    private final String[] mapping = new String[]{
                "email",
                "password",
                "name",
                "description",
                "active",
                "total_transaction_sum"
    };

    private static final UserCsvMappingStrategy USER_CSV_MAPPING_STRATEGY = new UserCsvMappingStrategy();

    public static MappingStrategy<User> instance() {
        return USER_CSV_MAPPING_STRATEGY;
    }

    @Override
    public String getColumnName(int col) {
        return mapping[col];
    }

    @Override
    public String[] getColumnMapping() {
        return mapping;
    }

    @Override
    public String[] generateHeader(User bean) throws CsvRequiredFieldEmptyException {
        return mapping;
    }

    @Override
    public User populateNewBean(String[] line) throws CsvBeanIntrospectionException, CsvFieldAssignmentException, CsvChainedException {
        User user = new User();

        user.setEmail(line[0]);
        user.setPassword(line[1]);
        if (!line[2].isBlank()) {
            Merchant merchant = new Merchant();
            merchant.setName(line[2]);
            merchant.setDescription(line[3]);
            merchant.setActive(Boolean.parseBoolean(line[4]));
            merchant.setTotalTransactionSum(new BigDecimal(line[5]));
            merchant.setName(line[2]);
            user.setMerchant(merchant);
        }
        return user;
    }

    @Override
    public String[] transmuteBean(User user) throws CsvFieldAssignmentException, CsvChainedException {

        if (user.getMerchant() != null) {
            return new String[]{
                    user.getEmail(),
                    user.getPassword(),
                    user.getMerchant().getName(),
                    user.getMerchant().getDescription(),
                    String.valueOf(user.getMerchant().isActive()),
                    user.getMerchant().getTotalTransactionSum().toString()
            };
        } else {
            return new String[]{
                    user.getEmail(),
                    user.getPassword(),
                    "",
                    "",
                    "",
                    ""
            };
        }
    }
}

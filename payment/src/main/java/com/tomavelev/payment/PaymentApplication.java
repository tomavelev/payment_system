package com.tomavelev.payment;

import com.opencsv.exceptions.*;
import com.tomavelev.payment.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class PaymentApplication {

    public static void main(String[] args) {
        if (args != null && args.length == 2) {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(PaymentApplication.class, args);
            UserService bean = applicationContext.getBean(UserService.class);
            if (args[0].equals("import")) {
                try {
                    bean.importFromSCV(new File(args[1]));
                } catch (CsvChainedException | CsvFieldAssignmentException| IOException e) {
                    //handle it according to requirements.
                    //if import is important - throw. Otherwise - silence it.
                    throw new RuntimeException(e);
                }
            } else if (args[0].equals("generate")) {
                try {
                    bean.generateSCV(args[1]);
                } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
                    //handle it according to requirements.
                    //if generate is important - throw. Otherwise - silence it.
                    throw new RuntimeException(e);
                }
            }
        } else {
            SpringApplication.run(PaymentApplication.class, args);
        }

    }

}

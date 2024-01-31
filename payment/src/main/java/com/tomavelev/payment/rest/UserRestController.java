package com.tomavelev.payment.rest;

import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;


    @PostMapping("/admin/importCsv")
    public void importCsv(@RequestParam("file") MultipartFile file) throws CsvChainedException, IOException, CsvFieldAssignmentException {

        try {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), "");
            file.transferTo(tempFile);
            userService.importFromSCV(tempFile);
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        } catch (Exception e){
            //silent duplicates
            //this ignore potentially could be moved to the service
        }
    }

    @PutMapping("/admin/user")
    public String update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/admin/user")
    public void delete(@RequestBody User user) {
        userService.delete(user);
    }

    @GetMapping(value = "/admin/users")
    public RestResponse<User> users(@RequestParam(value = "offset", defaultValue = "0") long offset,
                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return userService.getUsers(offset, limit);
    }

}

package com.tomavelev.payment.rest;

import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.service.MerchantService;
import com.tomavelev.payment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/admin/users")
    public RestResponse<User> users(@RequestParam(value = "offset", defaultValue = "0") long offset,
                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return userService.getUsers(offset, limit);
    }

}

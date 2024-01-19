package com.tomavelev.payment.service;

import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public RestResponse<User> getUsers(long offset, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        Page<User> page = userRepository.findAll(PageRequest.ofSize(limit).withPage((int) (offset / limit)));
        return new RestResponse<>(page.get().toList(), page.getTotalElements(), null, BusinessCode.SUCCESS);
    }
}

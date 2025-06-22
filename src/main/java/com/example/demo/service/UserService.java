package com.example.demo.service;

import com.example.demo.dto.UserRechargeDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void rechargeUser(UserRechargeDTO dto) {
        if (dto.getAmount().signum() < 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.recharge(dto.getAmount());
        userRepository.save(user);
    }
}

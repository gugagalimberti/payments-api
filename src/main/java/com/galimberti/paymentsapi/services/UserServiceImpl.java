package com.galimberti.paymentsapi.services;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galimberti.paymentsapi.domain.User;
import com.galimberti.paymentsapi.exceptions.PaAuthException;
import com.galimberti.paymentsapi.repositories.UserRepository;


@Service 
@Transactional 
public class UserServiceImpl implements UserService {

    @Autowired 
    UserRepository userRepository;

    @Override
    public User registerUser(String firstName, String lastName, String email, String password) throws PaAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null) email = email.toLowerCase();
        if(!pattern.matcher(email).matches())
            throw new PaAuthException("Invalid email format");
        Integer count = userRepository.getCountByemail(email);
        if(count > 0)
            throw new PaAuthException("Email already in use");
        Integer userId = userRepository.create(firstName, lastName, email, password);
        return userRepository.findById(userId);
    }

    @Override
    public User validateUser(String email, String password) throws PaAuthException {
        if(email != null) email = email.toLowerCase();
        return userRepository.findByEmailAndPassword(email, password);
    }

}

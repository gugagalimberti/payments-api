package com.galimberti.paymentsapi.services;

import com.galimberti.paymentsapi.domain.User;
import com.galimberti.paymentsapi.exceptions.PaAuthException;

public interface UserService {

    User validateUser(String email, String password) throws PaAuthException;

    User registerUser(String firstName, String lastName, String email, String password) throws PaAuthException;
    
}

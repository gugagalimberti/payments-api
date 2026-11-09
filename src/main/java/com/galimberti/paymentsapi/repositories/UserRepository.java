package com.galimberti.paymentsapi.repositories;

import com.galimberti.paymentsapi.domain.User;
import com.galimberti.paymentsapi.exceptions.PaAuthException;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws PaAuthException;

    User findByEmailAndPassword(String email, String password) throws PaAuthException;

    Integer getCountByemail(String email);

    User findById(Integer userId);

}

package com.galimberti.paymentsapi.repositories;

import java.util.List;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;

public interface CategoryRepository {

    List<Category> findAll(Integer userId) throws PaResourceNotFoundException;

    Category findById(Integer userId, Integer categoryId) throws PaResourceNotFoundException;

    Integer create(Integer userId, String title, String description) throws PaBadRequestException;

    void update(Integer userId, Integer categoryId, Category category) throws PaBadRequestException;

    void removeById(Integer userId, Integer categoryId);

}

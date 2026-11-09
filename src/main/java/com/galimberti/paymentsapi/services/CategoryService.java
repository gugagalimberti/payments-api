package com.galimberti.paymentsapi.services;

import java.util.List;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;

public interface CategoryService {

    List<Category> fetchAllCategories(Integer userId);

    Category fetchCategoryById(Integer userId, Integer categoryId) throws PaResourceNotFoundException;

    Category addCategory(Integer userId, String title, String description) throws PaBadRequestException;

    void updateCategory(Integer userId, Integer categoryId, Category category) throws PaBadRequestException;

    void removeCategoryWithAllTransactions(Integer userId, Integer categoryId) throws PaResourceNotFoundException;

}

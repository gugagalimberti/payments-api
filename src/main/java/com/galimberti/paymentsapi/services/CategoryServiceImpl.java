package com.galimberti.paymentsapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;
import com.galimberti.paymentsapi.repositories.CategoryRepository;

@Service 
@Transactional 
public class CategoryServiceImpl implements CategoryService {


    @Autowired 
    CategoryRepository categoryRepository;

    @Override
    public Category addCategory(Integer userId, String title, String description) throws PaBadRequestException {
        int categoryId = categoryRepository.create(userId, title, description);
        return categoryRepository.findById(userId, categoryId);
    }

    @Override
    public List<Category> fetchAllCategories(Integer userId) {
        return categoryRepository.findAll(userId);
    }

    @Override
    public Category fetchCategoryById(Integer userId, Integer categoryId) throws PaResourceNotFoundException {
        return categoryRepository.findById(userId, categoryId);
    }

    @Override
    public void removeCategoryWithAllTransactions(Integer userId, Integer categoryId)
            throws PaResourceNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCategory(Integer userId, Integer categoryId, Category category) throws PaBadRequestException {
        categoryRepository.update(userId, categoryId, category);
    }

}

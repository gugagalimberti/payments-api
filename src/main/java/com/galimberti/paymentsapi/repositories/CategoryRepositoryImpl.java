package com.galimberti.paymentsapi.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;

@Repository 
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String SQL_FIND_ALL = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +  
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM PA_TRANSACTIONS T RIGHT OUTER JOIN PA_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? GROUP BY C.CATEGORY_ID";
    private static final String SQL_FIND_BY_ID = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +  
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM PA_TRANSACTIONS T RIGHT OUTER JOIN PA_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? AND C.CATEGORY_ID = ? GROUP BY C.CATEGORY_ID";
    private static final String SQL_CREATE = "insert into pa_categories (category_id, user_id, title, description) values (nextval('pa_categories_seq'), ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE PA_CATEGORIES SET TITLE = ?, DESCRIPTION = ? WHERE USER_ID = ? AND CATEGORY_ID = ?";
    private static final String SQL_DELETE_CATEGORY = "DELETE FROM PA_CATEGORIES where USER_ID = ? AND CATEGORY_ID = ?";
    private static final String SQL_DELETE_ALL_CATEGORIES = "DELETE FROM PA_TRANSACTIONS where CATEGORY_ID = ?";

    @Autowired 
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(Integer userId, String title, String description)
            throws PaBadRequestException {

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    SQL_CREATE,
                    new String[] {"category_id"}
                );

                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);

                return ps;
            }, keyHolder);

            return keyHolder.getKey().intValue();

        } catch (Exception e) {
            throw new PaBadRequestException("Invalid request");
        }
    }
    
    @Override
    public List<Category> findAll(Integer userId) throws PaResourceNotFoundException {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId}, categoryRowMapper);
        } catch (Exception e) {
            throw new PaResourceNotFoundException("Category for this user doesnt exist");
        }
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws PaResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, categoryId}, categoryRowMapper);
        } catch (Exception e) {
            throw new PaResourceNotFoundException("Category not found");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {
        this.removeAllCatTransactions(categoryId);
        jdbcTemplate.update(SQL_DELETE_CATEGORY, new Object[]{userId, categoryId});
    }

    private void removeAllCatTransactions(Integer categoryId){
        jdbcTemplate.update(SQL_DELETE_ALL_CATEGORIES, new Object[]{categoryId});
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws PaBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{category.getTitle(), category.getDescription(), userId, categoryId});
        } catch (Exception e) {
            throw new PaBadRequestException("Invalid request");
        }
        
    }

    private RowMapper<Category> categoryRowMapper = ((rs, rowNum) -> {
        return new Category(rs.getInt("CATEGORY_ID"), rs.getInt("USER_ID"), rs.getString("TITLE"), rs.getString("DESCRIPTION"), rs.getDouble("TOTAL_EXPENSE"));
    });

}

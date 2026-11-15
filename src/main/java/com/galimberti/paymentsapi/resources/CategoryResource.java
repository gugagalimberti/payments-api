package com.galimberti.paymentsapi.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.dto.CategoryRequest;
import com.galimberti.paymentsapi.services.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categorias", description = "Categorias de pagamentos do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado", content = @Content)
@RestController 
@RequestMapping ("/api/categories")
public class CategoryResource {

    @Autowired 
    CategoryService categoryService;

    @Operation(summary = "Lista as categorias do usuário",
               description = "Cada categoria inclui o total gasto (totalExpense) com suas transações.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias (pode ser vazia)")
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(HttpServletRequest request) {
        int userId = (Integer) request.getAttribute("userId");
        List<Category> categories = categoryService.fetchAllCategories(userId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @Operation(summary = "Busca uma categoria pelo id")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não existe ou pertence a outro usuário", content = @Content)
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(HttpServletRequest request, @PathVariable("categoryId") Integer categoryId){
        int userId = (Integer) request.getAttribute("userId");
        Category category = categoryService.fetchCategoryById(userId, categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
    
    @Operation(summary = "Cria uma categoria")
    @ApiResponse(responseCode = "201", description = "Categoria criada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @PostMapping("")
    public ResponseEntity<Category> addCategory(HttpServletRequest request,
                                                @Valid @RequestBody CategoryRequest body) {
        int userId = (Integer) request.getAttribute("userId");
        Category category = categoryService.addCategory(userId, body.title(), body.description());
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza título e descrição de uma categoria")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @PutMapping("/{categoryId}")
    public ResponseEntity<Map<String, Boolean>> updateCategory(HttpServletRequest request,
            @PathVariable("categoryId") Integer categoryId,
            @Valid @RequestBody CategoryRequest body) {
        int userId = (Integer) request.getAttribute("userId");
        Category category = new Category(categoryId, userId, body.title(), body.description(), null);
        categoryService.updateCategory(userId, categoryId, category);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Operation(summary = "Remove uma categoria e todas as suas transações")
    @ApiResponse(responseCode = "200", description = "Categoria removida")
    @ApiResponse(responseCode = "404", description = "Categoria não existe ou pertence a outro usuário", content = @Content)
    @DeleteMapping ("/{categoryId}")
    public ResponseEntity<Map<String, Boolean>> deleteCategory(HttpServletRequest request, @PathVariable ("categoryId") Integer categoryId) {
        int userId = (Integer) request.getAttribute("userId");
        categoryService.removeCategoryWithAllTransactions(userId, categoryId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}

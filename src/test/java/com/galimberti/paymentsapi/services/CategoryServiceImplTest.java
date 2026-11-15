package com.galimberti.paymentsapi.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.galimberti.paymentsapi.domain.Category;
import com.galimberti.paymentsapi.exceptions.PaBadRequestException;
import com.galimberti.paymentsapi.exceptions.PaResourceNotFoundException;
import com.galimberti.paymentsapi.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void deveRealizarCadastro() {
        // Arrange
        Category categoria = new Category(2, 1, "titulo", "descricao", 0.0);
        when(categoryRepository.findById(1, 2)).thenReturn(categoria);
        when(categoryRepository.create(1, "titulo", "descricao")).thenReturn(2);

        // Act
        Category resultado = categoryService.addCategory(1, "titulo", "descricao");

        // Assert
        assertSame(categoria, resultado);
    }

    @Test
    void deveRejeitarCadastro() {
        // Arrange
        when(categoryRepository.create(1, "titulo", "descricao"))
                .thenThrow(new PaBadRequestException("Invalid request"));

        // Act + Assert
        assertThrows(PaBadRequestException.class,
                () -> categoryService.addCategory(1, "titulo", "descricao"));

        // Nenhuma categoria pode ser gravada
        verify(categoryRepository, never()).findById(any(), any());
    }

    @Test
    void deveRetornarTodasCategorias() {
        // Arrange
        Category categoria1 = new Category(2, 1, "titulo", "descricao", 0.0);
        Category categoria2 = new Category(3, 1, "titulo2", "descricao2", 0.0);
        List<Category> categoriaReferencia = List.of(categoria1, categoria2);
        when(categoryRepository.findAll(1)).thenReturn(categoriaReferencia);

        // Act
        List<Category> resultado = categoryService.fetchAllCategories(1);

        // Assert
        assertSame(categoriaReferencia, resultado);
    }

    @Test
    void deveRetornarCategoriaDoId() {
        // Arrange
        Category categoriaReferencia = new Category(2, 1, "titulo", "descricao", 0.0);
        when(categoryRepository.findById(1, 2)).thenReturn(categoriaReferencia);

        // Act
        Category resultado = categoryService.fetchCategoryById(1, 2);

        // Assert
        assertSame(categoriaReferencia, resultado);
    }

    @Test
    void deveRejeitarConsultaCategoriaInexistente() {
        // Arrange
        when(categoryRepository.findById(1, 2))
                .thenThrow(new PaResourceNotFoundException("Category not found"));

        // Act + Assert
        assertThrows(PaResourceNotFoundException.class,
                () -> categoryService.fetchCategoryById(1, 2));
    }

    @Test
    void deveAtualizarCategoria() {
        // Arrange
        Category categoria = new Category(2, 1, "titulo", "descricao", 0.0);

        // Act
        categoryService.updateCategory(1, 2, categoria);

        // Assert
        verify(categoryRepository).update(1, 2, categoria);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaComDadosInvalidos() {
        // Arrange
        Category categoria = new Category(2, 1, "titulo", "descricao", 0.0);
        doThrow(new PaBadRequestException("Invalid request"))
                .when(categoryRepository).update(1, 2, categoria);

        // Act + Assert
        assertThrows(PaBadRequestException.class,
                () -> categoryService.updateCategory(1, 2, categoria));
    }

    @Test
    void deveExcluirTodasTransacoesDeCategoriaECategoria() {
        // Arrange
        Category categoria = new Category(2, 1, "titulo", "descricao", 0.0);
        when(categoryRepository.findById(1, 2)).thenReturn(categoria);

        // Act
        categoryService.removeCategoryWithAllTransactions(1, 2);

        // Assert
        InOrder ordem = inOrder(categoryRepository);
        ordem.verify(categoryRepository).findById(1, 2);
        ordem.verify(categoryRepository).removeById(1, 2);
    }

    @Test
    void deveLancarExcecaoAoRemoverCategoriaInexistente() {
        // Arrange
        doThrow(new PaResourceNotFoundException("Category not found"))
                .when(categoryRepository).findById(1, 2);

        // Act + Assert
        assertThrows(PaResourceNotFoundException.class,
                () -> categoryService.removeCategoryWithAllTransactions(1, 2));
        verify(categoryRepository, never()).removeById(1, 2);
    }
}

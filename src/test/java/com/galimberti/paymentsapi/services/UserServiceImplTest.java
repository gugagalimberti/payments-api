package com.galimberti.paymentsapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.galimberti.paymentsapi.domain.User;
import com.galimberti.paymentsapi.exceptions.PaAuthException;
import com.galimberti.paymentsapi.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void deveRejeitarEmailJaCadastrado() {
        // Arrange: prepara o cenário
        when(userRepository.getCountByemail("ana@teste.com")).thenReturn(1);

        // Act + Assert: executa e confere a exceção
        assertThrows(PaAuthException.class,
            () -> userService.registerUser("Ana", "Silva", "ana@teste.com", "123"));

        // Confere que nada foi criado
        verify(userRepository, never()).create(any(), any(), any(), any());
    }

    @Test
    void deveRejeitarEmailFormatoErrado() {
        // Arrange: prepara o cenário
        String emailInvalido = "anateste.com";

        // Act + Assert: executa e confere a exceção
        PaAuthException e = assertThrows(PaAuthException.class,
            () -> userService.registerUser("Ana", "Silva", emailInvalido, "123"));
        assertEquals("Invalid email format", e.getMessage());
        
        // Confere que nada foi criado
        verify(userRepository, never()).create(any(), any(), any(), any());	
    }

    @Test
    void deveConverterEmailEmMaiusculo() {
        // Arrange: prepara o cenário
        String emailMaiusculo = "ANA@TESTE.COM";

        // Act + Assert: executa e confere a exceção
        userService.registerUser("Ana", "Silva", emailMaiusculo, "123");

        // Confere que foi criado com minusculo
		verify(userRepository).getCountByemail("ana@teste.com");
        verify(userRepository).create("Ana", "Silva", "ana@teste.com", "123");
    }

    @Test
    void deveRealizarCadastro() {
        // Arrange: o "banco" devolve o id 1 e depois o usuário com esse id
		User usuarioSalvo = new User(1, "Ana", "Silva", "ana@teste.com", "senhaComHash");
        when(userRepository.create("Ana", "Silva", "ana@teste.com", "123")).thenReturn(1);
		when(userRepository.findById(1)).thenReturn(usuarioSalvo);

        // Act 
		User resultado = userService.registerUser("Ana", "Silva", "ana@teste.com", "123");
		
		// Assert: o service devolve o usuário encontrado pelo id criado
		assertSame(usuarioSalvo, resultado);
    }

    @Test
    void deveRejeitarEmailNulo() {
        // Arrange: prepara o cenário
        String emailNulo = null;

        // Act + Assert: executa e confere a exceção
        PaAuthException e = assertThrows(PaAuthException.class,
            () -> userService.registerUser("Ana", "Silva", emailNulo, "123"));
        assertEquals("Invalid email format", e.getMessage());
        
        // Confere que nada foi criado
        verify(userRepository, never()).create(any(), any(), any(), any());	
    }

    	@Test
	    void deveValidarUsuarioComEmailEmMinusculo() {
		// Arrange
		User usuario = new User(1, "Ana", "Silva", "ana@teste.com", "senhaComHash");
		when(userRepository.findByEmailAndPassword("ana@teste.com", "123")).thenReturn(usuario);

		// Act
		User resultado = userService.validateUser("ANA@TESTE.COM", "123");

		// Assert
		assertSame(usuario, resultado);
	}
}

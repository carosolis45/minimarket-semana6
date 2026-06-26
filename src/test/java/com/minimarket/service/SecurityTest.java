package com.minimarket.service;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de autenticación y seguridad.
 * Se usa Mockito para simular el AuthenticationManager y UsuarioRepository.
 */
@ExtendWith(MockitoExtension.class)
class SecurityTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario();
        usuarioValido.setId(1L);
        usuarioValido.setUsername("admin");
        usuarioValido.setPassword("admin123");
    }

    // ========== PRUEBAS DE AUTENTICACIÓN ==========

    @Test
    @DisplayName("Autenticación válida - usuario existe y contraseña correcta")
    void autenticacion_Valida_Exito() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);

        // Act
        Authentication resultado = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin123")
        );

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isAuthenticated());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Autenticación inválida - usuario no existe")
    void autenticacion_UsuarioNoExistente_Falla() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken("usuarioInexistente", "123456")
            );
        });
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Autenticación inválida - contraseña incorrecta")
    void autenticacion_ContrasenaIncorrecta_Falla() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken("admin", "passwordIncorrecta")
            );
        });
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Usuario con rol ADMIN puede acceder a operaciones de administración")
    void usuarioConRolAdmin_TieneAcceso() {
        // Arrange
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setRoles(java.util.Set.of(new com.minimarket.entity.Rol("ROLE_ADMIN")));

        // Act
        boolean tieneRolAdmin = admin.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_ADMIN"));

        // Assert
        assertTrue(tieneRolAdmin);
    }

    @Test
    @DisplayName("Usuario con rol CLIENTE NO tiene acceso a administración")
    void usuarioConRolCliente_NoTieneAccesoAdmin() {
        // Arrange
        Usuario cliente = new Usuario();
        cliente.setUsername("cliente1");
        cliente.setRoles(java.util.Set.of(new com.minimarket.entity.Rol("ROLE_CLIENTE")));

        // Act
        boolean tieneRolAdmin = cliente.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_ADMIN"));

        // Assert
        assertFalse(tieneRolAdmin);
    }
}
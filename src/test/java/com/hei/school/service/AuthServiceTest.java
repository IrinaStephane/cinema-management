package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.RegisterRequestDTO;
import com.hei.school.entity.User;
import com.hei.school.entity.UserRole;
import com.hei.school.exception.EmailAlreadyUsedException;
import com.hei.school.repository.UserRepository;
import com.hei.school.security.JwtService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

  private UserRepository userRepository;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    JwtService jwtService = mock(JwtService.class);

    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(jwtService.generateToken(any())).thenReturn("fake-jwt");
    when(userRepository.save(any()))
        .thenAnswer(invocation -> invocation.getArgument(0, User.class));

    authService =
        new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
  }

  @Test
  void shouldRegisterNewUser() {
    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    var dto =
        new RegisterRequestDTO(
            "John",
            "Doe",
            LocalDate.of(2000, 1, 1),
            "test@example.com",
            "password123",
            "0340000000",
            UserRole.CLIENT);

    var response = authService.register(dto);

    assertThat(response.getToken()).isEqualTo("fake-jwt");
  }

  @Test
  void shouldRejectDuplicateEmail() {
    when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
    var dto =
        new RegisterRequestDTO(
            "John",
            "Doe",
            LocalDate.of(2000, 1, 1),
            "test@example.com",
            "password123",
            "0340000000",
            UserRole.CLIENT);

    assertThatThrownBy(() -> authService.register(dto))
        .isInstanceOf(EmailAlreadyUsedException.class);
  }
}

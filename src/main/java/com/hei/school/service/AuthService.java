package com.hei.school.service;

import com.hei.school.dto.request.LoginRequestDTO;
import com.hei.school.dto.request.RegisterRequestDTO;
import com.hei.school.dto.response.AuthResponseDTO;
import com.hei.school.entity.User;
import com.hei.school.exception.EmailAlreadyUsedException;
import com.hei.school.repository.UserRepository;
import com.hei.school.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Transactional
  public AuthResponseDTO register(RegisterRequestDTO dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new EmailAlreadyUsedException("Email already used: " + dto.getEmail());
    }

    User user =
        User.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .birthdate(dto.getBirthdate())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .phone(dto.getPhone())
            .role(dto.getRole())
            .build();

    User saved = userRepository.save(user);
    return AuthResponseDTO.builder().token(jwtService.generateToken(saved)).build();
  }

  public AuthResponseDTO login(LoginRequestDTO dto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

    User user =
        userRepository
            .findByEmail(dto.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + dto.getEmail()));

    return AuthResponseDTO.builder().token(jwtService.generateToken(user)).build();
  }
}

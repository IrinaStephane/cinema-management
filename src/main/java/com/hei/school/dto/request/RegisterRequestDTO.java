package com.hei.school.dto.request;

import com.hei.school.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

  @NotBlank(message = "firstName is required")
  private String firstName;

  @NotBlank(message = "lastName is required")
  private String lastName;

  @NotNull(message = "birthdate is required")
  private LocalDate birthdate;

  @NotBlank(message = "email is required")
  @Email(message = "email must be valid")
  private String email;

  @NotBlank(message = "password is required")
  private String password;

  private String phone;

  @NotNull(message = "role is required")
  private UserRole role;
}

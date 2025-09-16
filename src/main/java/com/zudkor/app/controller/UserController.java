package com.zudkor.app.controller;

import com.zudkor.app.entity.User;
import com.zudkor.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Регистрация пользователя без аутентификации
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Проверяем email
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email уже используется"));
        }

        // Проверяем username
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username уже используется"));
        }

        // Хешируем пароль
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Сохраняем пользователя
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 🔹 Получить всех пользователей
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 🔹 Получить пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(user))  // явно указываем, что это ResponseEntity<?>
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Пользователь не найден")));
    }

    // 🔹 Обновить пользователя
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    // Проверка уникальности email, только если новое значение не null
                if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())
                        && userRepository.existsByEmail(updatedUser.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Email уже используется"));
                }

                // Проверка уникальности username, только если новое значение не null
                if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())
                        && userRepository.existsByUsername(updatedUser.getUsername())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Username уже используется"));
                }

                // Обновляем только непустые значения
                if (updatedUser.getUsername() != null) {
                    user.setUsername(updatedUser.getUsername());
                }
                if (updatedUser.getEmail() != null) {
                    user.setEmail(updatedUser.getEmail());
                }

                // Если обновляется пароль — хэшируем
                if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
                    user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
                }

                User saved = userRepository.save(user);
                return ResponseEntity.ok(saved);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Пользователь не найден")));
    }

    // 🔹 Удалить пользователя для Админ
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
    //     return userRepository.findById(id)
    //             .map(user -> {
    //                 userRepository.delete(user);
    //                 return ResponseEntity.noContent().build();
    //             })
    //             .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                     .body(Map.of("error", "Пользователь не найден")));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        // Получаем username текущего пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        return userRepository.findById(id)
                .map(user -> {
                    // Проверяем, совпадает ли пользователь
                    if (!user.getUsername().equals(currentUsername)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Вы можете удалить только себя"));
                    }

                    userRepository.delete(user);
                    return ResponseEntity.ok(Map.of("message", "Пользователь успешно удалён"));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Пользователь не найден")));
    }
}
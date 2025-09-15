package com.zudkor.app.controller;

import com.zudkor.app.entity.User;
import com.zudkor.app.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 Получить всех пользователей
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // 🔹 Получить пользователя по ID
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 Создать нового пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    // 🔹 Обновить пользователя
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());
        return userRepository.save(existing);
    }

    // 🔹 Удалить пользователя
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}

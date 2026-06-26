package com.air.assetmanagement;

import com.air.assetmanagement.model.User;
import com.air.assetmanagement.repository.UserRepository;
import com.air.assetmanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testRepositoryLoaded() {
        assertNotNull(userRepository, "UserRepository debe estar disponible");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.findAll();

        assertFalse(users.isEmpty(), "La lista de usuarios no debe estar vacía");
        users.forEach(user -> System.out.println("Usuario: " + user.getId() + " - " + user.getName()));
        System.out.println("Total de usuarios encontrados: " + users.size());
    }

    @Test
    void testFindUserById() {
        List<User> users = userService.findAll();
        assertFalse(users.isEmpty(), "Debe haber al menos un usuario para buscar por ID");

        Long firstId = users.get(0).getId();
        Optional<User> found = userService.findById(firstId);

        assertTrue(found.isPresent(), "Debe encontrarse el usuario con id " + firstId);
        System.out.println("Usuario encontrado: " + found.get().getName());
    }
}

package ru.krbk.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krbk.authorization.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}

package ru.krbk.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krbk.authorization.entity.IntegrationLog;

public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Integer> {
}

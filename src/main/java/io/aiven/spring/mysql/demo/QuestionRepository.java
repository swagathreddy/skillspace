package io.aiven.spring.mysql.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // You can add custom query methods here if needed
}

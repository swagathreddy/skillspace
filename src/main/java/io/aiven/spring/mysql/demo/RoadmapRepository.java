package io.aiven.spring.mysql.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    // You can add custom query methods here if needed
}


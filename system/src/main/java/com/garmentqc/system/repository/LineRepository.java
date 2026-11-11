package com.garmentqc.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garmentqc.system.enums.Section;
import com.garmentqc.system.model.Line;

public interface LineRepository extends JpaRepository<Line, String> {
    List<Line> findBySection(String section);

    boolean existsByNameAndSection(String name, Section section);

    boolean existsByNameAndSectionAndIdNot(String name, Section section, String id);

    List<Line> findBySectionAndActiveTrue(Section section);

    Optional<Line> findByIdAndSectionAndActiveTrue(String id, Section section);

}

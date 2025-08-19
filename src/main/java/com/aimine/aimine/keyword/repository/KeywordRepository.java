package com.aimine.aimine.keyword.repository;

import com.aimine.aimine.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByName(String name);

    List<Keyword> findByType(Keyword.KeywordType type);

    boolean existsByName(String name);

    @Query("SELECT k FROM Keyword k ORDER BY k.name ASC")
    List<Keyword> findAllOrderByName();
}
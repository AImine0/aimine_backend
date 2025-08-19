package com.aimine.aimine.keyword.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.keyword.domain.AiServiceKeyword;
import com.aimine.aimine.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiServiceKeywordRepository extends JpaRepository<AiServiceKeyword, Long> {

    // 특정 AI 서비스의 키워드 목록
    @Query("SELECT ask.keyword FROM AiServiceKeyword ask WHERE ask.aiService = :aiService")
    List<Keyword> findKeywordsByAiService(@Param("aiService") AiService aiService);

    // 특정 키워드를 가진 AI 서비스 목록
    @Query("SELECT ask.aiService FROM AiServiceKeyword ask WHERE ask.keyword = :keyword")
    List<AiService> findAiServicesByKeyword(@Param("keyword") Keyword keyword);

    // 키워드별 AI 서비스 개수
    @Query("SELECT COUNT(ask) FROM AiServiceKeyword ask WHERE ask.keyword = :keyword")
    Long countAiServicesByKeyword(@Param("keyword") Keyword keyword);

    // AI 서비스와 키워드 연결 삭제
    void deleteByAiServiceAndKeyword(AiService aiService, Keyword keyword);

    // AI 서비스의 모든 키워드 연결 삭제
    void deleteByAiService(AiService aiService);
}
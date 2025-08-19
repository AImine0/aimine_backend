package com.aimine.aimine.aicombination.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aicombination.domain.AiCombination;
import com.aimine.aimine.aicombination.domain.AiCombinationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiCombinationServiceRepository extends JpaRepository<AiCombinationService, Long> {

    // 특정 조합의 AI 서비스 목록
    @Query("SELECT acs.aiService FROM AiCombinationService acs WHERE acs.combination = :combination")
    List<AiService> findAiServicesByCombination(@Param("combination") AiCombination combination);

    // 특정 AI 서비스가 포함된 조합 목록
    @Query("SELECT acs.combination FROM AiCombinationService acs WHERE acs.aiService = :aiService")
    List<AiCombination> findCombinationsByAiService(@Param("aiService") AiService aiService);

    // 조합별 AI 서비스 개수
    @Query("SELECT COUNT(acs) FROM AiCombinationService acs WHERE acs.combination = :combination")
    Long countAiServicesByCombination(@Param("combination") AiCombination combination);

    // 조합과 AI 서비스 연결 삭제
    void deleteByCombinationAndAiService(AiCombination combination, AiService aiService);

    // 조합의 모든 AI 서비스 연결 삭제
    void deleteByCombination(AiCombination combination);
}
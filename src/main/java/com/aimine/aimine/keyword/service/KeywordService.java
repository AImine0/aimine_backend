package com.aimine.aimine.keyword.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.bookmark.repository.BookmarkRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import com.aimine.aimine.keyword.domain.Keyword;
import com.aimine.aimine.keyword.dto.KeywordByTypeResponse;
import com.aimine.aimine.keyword.dto.KeywordListResponse;
import com.aimine.aimine.keyword.dto.KeywordServiceListResponse;
import com.aimine.aimine.keyword.repository.AiServiceKeywordRepository;
import com.aimine.aimine.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final AiServiceKeywordRepository aiServiceKeywordRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 모든 키워드 목록 조회
     */
    public KeywordListResponse getAllKeywords() {
        log.debug("키워드 전체 목록 조회 요청");

        List<Keyword> keywords = keywordRepository.findAllOrderByName();

        // 각 키워드별 AI 서비스 개수 조회
        List<Long> toolCounts = keywords.stream()
                .map(aiServiceKeywordRepository::countAiServicesByKeyword)
                .collect(Collectors.toList());

        return KeywordListResponse.from(keywords, toolCounts);
    }

    /**
     * 키워드 타입별 조회
     */
    public KeywordByTypeResponse getKeywordsByType(String type) {
        log.debug("키워드 타입별 조회 요청: type={}", type);

        Keyword.KeywordType keywordType;
        try {
            keywordType = Keyword.KeywordType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(AiServiceErrorCode.INVALID_FILTER_PARAMETER,
                    "유효하지 않은 키워드 타입: " + type);
        }

        List<Keyword> keywords = keywordRepository.findByType(keywordType);

        // 각 키워드별 AI 서비스 개수 조회
        List<Long> toolCounts = keywords.stream()
                .map(aiServiceKeywordRepository::countAiServicesByKeyword)
                .collect(Collectors.toList());

        return KeywordByTypeResponse.from(keywords, toolCounts);
    }

    /**
     * 특정 키워드의 AI 서비스 목록 조회
     */
    public KeywordServiceListResponse getAiServicesByKeyword(Long keywordId) {
        log.debug("키워드별 AI 서비스 목록 조회 요청: keywordId={}", keywordId);

        // 키워드 조회
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.KEYWORD_NOT_FOUND));

        // 해당 키워드를 가진 AI 서비스 목록 조회
        List<AiService> aiServices = aiServiceKeywordRepository.findAiServicesByKeyword(keyword);

        // 각 AI 서비스별 북마크 개수 조회
        List<Long> bookmarkCounts = aiServices.stream()
                .map(bookmarkRepository::countByAiService)
                .collect(Collectors.toList());

        return KeywordServiceListResponse.from(keyword, aiServices, bookmarkCounts);
    }

    /**
     * 키워드 이름으로 조회
     */
    public Keyword findByName(String name) {
        return keywordRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.KEYWORD_NOT_FOUND));
    }

    /**
     * 키워드 ID로 조회
     */
    public Keyword findById(Long keywordId) {
        return keywordRepository.findById(keywordId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.KEYWORD_NOT_FOUND));
    }
}
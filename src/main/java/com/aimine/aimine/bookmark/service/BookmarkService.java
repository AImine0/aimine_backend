package com.aimine.aimine.bookmark.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.bookmark.domain.Bookmark;
import com.aimine.aimine.bookmark.dto.BookmarkCreateRequest;
import com.aimine.aimine.bookmark.dto.BookmarkCreateResponse;
import com.aimine.aimine.bookmark.dto.BookmarkDeleteResponse;
import com.aimine.aimine.bookmark.dto.BookmarkListResponse;
import com.aimine.aimine.bookmark.repository.BookmarkRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import com.aimine.aimine.common.exception.errorcode.BookmarkErrorCode;
import com.aimine.aimine.common.exception.errorcode.UserErrorCode;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final AiServiceRepository aiServiceRepository;

    /**
     * 북마크 추가
     */
    @Transactional
    public BookmarkCreateResponse createBookmark(Long userId, BookmarkCreateRequest request) {
        log.debug("북마크 추가 요청: userId={}, aiServiceId={}", userId, request.getAiServiceId());

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // AI 서비스 조회
        AiService aiService = aiServiceRepository.findById(request.getAiServiceId())
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

        // 이미 북마크가 존재하는지 확인
        if (bookmarkRepository.existsByUserAndAiService(user, aiService)) {
            throw new BusinessException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        // 북마크 생성 및 저장
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .aiService(aiService)
                .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        log.info("북마크 추가 완료: bookmarkId={}, userId={}, aiServiceId={}",
                savedBookmark.getId(), userId, request.getAiServiceId());

        return BookmarkCreateResponse.from(savedBookmark);
    }

    /**
     * 북마크 제거
     */
    @Transactional
    public BookmarkDeleteResponse deleteBookmark(Long userId, Long serviceId) {
        log.debug("북마크 제거 요청: userId={}, serviceId={}", userId, serviceId);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // AI 서비스 조회
        AiService aiService = aiServiceRepository.findById(serviceId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

        // 북마크 조회
        Bookmark bookmark = bookmarkRepository.findByUserAndAiService(user, aiService)
                .orElseThrow(() -> new BusinessException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));

        // 북마크 삭제
        bookmarkRepository.delete(bookmark);

        log.info("북마크 제거 완료: userId={}, serviceId={}", userId, serviceId);

        return BookmarkDeleteResponse.success();
    }

    /**
     * 내 북마크 목록 조회
     */
    public BookmarkListResponse getMyBookmarks(Long userId) {
        log.debug("북마크 목록 조회 요청: userId={}", userId);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 북마크 목록 조회 (최신순)
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));
        Page<Bookmark> bookmarksPage = bookmarkRepository.findByUser(user, pageable);
        List<Bookmark> bookmarks = bookmarksPage.getContent();

        return BookmarkListResponse.from(bookmarks);
    }

    /**
     * 북마크 존재 확인
     */
    public boolean isBookmarked(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElse(null);
        AiService aiService = aiServiceRepository.findById(serviceId).orElse(null);

        if (user == null || aiService == null) {
            return false;
        }

        return bookmarkRepository.existsByUserAndAiService(user, aiService);
    }

    /**
     * 사용자의 북마크 개수 조회
     */
    public long getBookmarkCount(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return 0;
        }

        return bookmarkRepository.countByUser(user);
    }
}
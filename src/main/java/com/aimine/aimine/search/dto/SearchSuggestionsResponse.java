package com.aimine.aimine.search.dto;

import com.aimine.aimine.search.dto.SearchSuggestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestionsResponse {
    private List<SearchSuggestion> suggestions;
}
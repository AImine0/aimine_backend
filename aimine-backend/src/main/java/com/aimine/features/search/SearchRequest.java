package com.aimine.features.search;

public record SearchRequest(String q, Integer page, Integer size) {
}
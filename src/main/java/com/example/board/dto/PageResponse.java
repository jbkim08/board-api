package com.example.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;   // 실제 데이터 목록
    private int page;          // 현재 페이지 (1부터 시작)
    private int size;          // 페이지당 개수
    private long totalElements; // 전체 데이터 개수
    private int totalPages;     // 전체 페이지 수

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PageResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}


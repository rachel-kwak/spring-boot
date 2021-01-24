package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// JPA 쪽에서 사용하는 Pageable 타입의 객체를 생성
@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {

    private int page;
    private int size;
    private String type;
    private String keyword;

    public PageRequestDTO(){
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort){
        // JPA를 이용하는 경우 페이지 번호가 0번부터 시작하기 때문에 page에서 1을 뺀다.
        return PageRequest.of(page - 1, size, sort);
    }
}
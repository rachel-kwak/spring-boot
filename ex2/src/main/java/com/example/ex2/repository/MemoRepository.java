package com.example.ex2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ex2.entity.Memo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Spring Data JPA는 인터페이스 선언만으로도 자동으로 스프링의 빈으로 등록
public interface MemoRepository extends JpaRepository<Memo, Long> {
    // mno를 기준으로 between 구문을 사용하고 order by가 적용
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to); // Query 메서드를 위해 추가
    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable); // Query 메서드와 Pageable 결합
    void deleteMemoByMnoLessThan(Long num); // mno가 num보다 작은 데이터를 삭제
}
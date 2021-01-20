package com.example.ex2.entity;

import lombok.*;
import javax.persistence.*;

// 엔티티 클래스로 데이터베이스의 테이블과 같은 구조를 작성
@Entity // 엔티티 클래스는 반드시 추가 필요
@Table(name="tbl_memo")
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;

    @Column(length = 200, nullable = false)
    private String memoText;
}

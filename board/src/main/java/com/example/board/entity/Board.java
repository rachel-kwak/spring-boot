package com.example.board.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
// Member 객체의 toString()이 호출되어야 하므로 데이터베이스 연결이 필요하게 됨
// 연관관계가 있는 엔티티 클래스의 경우 exclude를 명시해 주는 것이 좋으며,
// 지연 로딩을 할 때는 반드시 지정해줘야 한다.
@ToString(exclude = "writer")
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    private String title;
    private String content;

    @ManyToOne (fetch = FetchType.LAZY) // 명시적으로 지연 로딩 지정
    private Member writer;
}
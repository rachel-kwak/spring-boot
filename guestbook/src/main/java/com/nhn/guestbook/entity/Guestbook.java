package com.nhn.guestbook.entity;

import lombok.*;
import javax.persistence.*;

// 기존의 엔티티 클래스를 작성할 때와 달리 BaseEntity를 상속할 것임
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Guestbook extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    // 원래 엔티티 클래스는 가능하면 setter 관련 기능을 만들지 않지만,
    // 필요에 따라서 수정 기능을 만들기도 한다.
    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}

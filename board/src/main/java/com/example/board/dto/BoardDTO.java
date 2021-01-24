package com.example.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Board 엔티티 클래스와 다른 점?
// Member를 참조하는 대신에 작성자의 이메일과 이름을 사용
// 목록에서 BoardDTO를 이용하기 떄문에 댓글의 개수도 필요
public class BoardDTO {
    private Long bno;
    private String title;
    private String content;
    private String writerEmail; // 작성자의 이메일
    private String writerName;  // 작성자 이름
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int replyCount; // 해당 게시글의 댓글 수
}

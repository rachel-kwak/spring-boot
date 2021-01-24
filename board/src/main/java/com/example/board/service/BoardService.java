package com.example.board.service;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResultDTO;
import com.example.board.entity.Board;
import com.example.board.entity.Member;

public interface BoardService {
    Long register(BoardDTO dto);    // BoardDTO 타입을 파라미터로 전달받고, 생성된 게시물의 번호를 반환
    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);   // 목록 처리
    BoardDTO get(Long bno);
    void removeWithReplies(Long bno);
    void modify(BoardDTO boardDTO);

    // 실제 처리 과정에서 BoardDTO를 Board 엔티티 타입으로 변환할 필요가 있는데, 아래 함수를 사용
    default Board dtoToEntity(BoardDTO dto) {
        Member member = Member.builder().email(dto.getWriterEmail()).build();
        Board board = Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .build();
        return board;
    }

    // Board와 Member, Long 타입을 가진 Object[]를 DTO로 변환해줘야함
    // Board, Member, replyCount를 다 합쳐서 DTO로 만들어주는 함수
    default BoardDTO entityToDTO(Board board, Member member, Long replyCount) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerEmail(member.getEmail())
                .writerName(member.getName())
                .replyCount(replyCount.intValue()) //int로 처리하도록
                .build();

        return boardDTO;
    }
}

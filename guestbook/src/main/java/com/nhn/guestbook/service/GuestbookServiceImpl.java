package com.nhn.guestbook.service;


import com.nhn.guestbook.entity.QGuestbook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.nhn.guestbook.dto.GuestbookDTO;
import com.nhn.guestbook.dto.PageRequestDTO;
import com.nhn.guestbook.dto.PageResultDTO;
import com.nhn.guestbook.entity.Guestbook;
import com.nhn.guestbook.repository.GuestbookRepository;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor    // 의존성 자동 주입
public class GuestbookServiceImpl implements GuestbookService {
    private final GuestbookRepository repository;   // 반드시 final로 선언
    @Override
    public Long register(GuestbookDTO dto) {
        log.info("========== DTO ==========");
        log.info(dto);

        Guestbook entity = dtoToEntity(dto);
        log.info(entity);

        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {

        // entityToDTO를 이용해서 java.util.Function을 생성하고 이를 PageResultDTO로 구성
        // PageResultDTO에 JPA의 처리 결과인 Page<Entity>와 Function을 전달해서
        // 엔티티 객체들을 DTO의 리스트로 변환하고, 화면에 페이지 처리와 필요한 값들을 생성
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        /* 검색을 위해 추가 및 변경 된 부분 */
        BooleanBuilder booleanBuilder = getSearch(requestDTO); // 검색 조건 처리
        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable); // Querydsl 사용
        // Page<Guestbook> result = repository.findAll(pageable);

        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);
        // findById()를 통해서 객체를 가져왔다면 앤티티 객체를 DTO로 변환해서 반환
        return result.isPresent()? entityToDto(result.get()): null;
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto) {
        // 업데이트 하는 항목은 제목과 내용
        Optional<Guestbook> result = repository.findById(dto.getGno());
        if (result.isPresent()) {
            Guestbook entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }

    // PageRequestDTO를 파라미터로 받아서 검색 조건(type)이 있는 경우 conditionBuilder 변수를 생성해 검색 조건을 or로 연결함
    // 검색 조건이 없다면 gno > 0으로만 생성됨
    private BooleanBuilder getSearch(PageRequestDTO requestDTO){
        String type = requestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = requestDTO.getKeyword();
        BooleanExpression expression = qGuestbook.gno.gt(0L); // gno > 0 조건만 생성

        booleanBuilder.and(expression);
        if(type == null || type.trim().length() == 0){ //검색 조건이 없는 경우
            return booleanBuilder;
        }
        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t"))
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        if(type.contains("c"))
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        if(type.contains("w"))
            conditionBuilder.or(qGuestbook.writer.contains(keyword));

        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }
}

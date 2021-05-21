package com.nhn.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.nhn.guestbook.entity.Guestbook;
import com.nhn.guestbook.entity.QGuestbook;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies(){
        // 300개의 데이터를 생성
        IntStream.rangeClosed(1,300).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...." + i)
                    .content("Content..." +i)
                    .writer("user" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest() {

        Optional<Guestbook> result = guestbookRepository.findById(300L); // 존재하는 번호로 테스트

        if(result.isPresent()){
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title....");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);    // save할 때 300번의 moddate 바뀜
        }
    }

    // 페이지 처리와 동시에 검색이 가능
    // 제목에 '1'이 들어간 엔티티를 gno 역순으로 10개
    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        // 1. 가장 먼저 동적으로 처리하기 위해서 Q도메인 클래스를 얻어온다.
        // Q도메인 클래스를 이용하면 엔티티 클래스에 선언된 title, content 같은 필드들을 변수로 활용할 수 있다.
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        // 2. where 문에 들어가는 조건들을 넣어주는 컨테이너
        BooleanBuilder builder = new BooleanBuilder();

        // 3. 원하는 조건을 필드 값과 결합해서 생성
        // BooleanBuilder 안에 들어가는 값은 com.querydsl.core.types.Predicate 타입
        BooleanExpression expression = qGuestbook.title.contains(keyword);

        // 4. 만들어진 조건은 where 문에 and나 or 같은 키워드와 결합
        builder.and(expression);

        // 5. QuerydslPredicateExcutor가 GuestbookRepository에 추가되어있어 findAll() 사용 가능
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });

    }

    // 다중 항목 검색
    // 제목에 '1'이 들어가거나 내용에 '1'이 들어가는 것 중 gno가 0보다 큰 것
    @Test
    public void testQuery2() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle =  qGuestbook.title.contains(keyword);
        BooleanExpression exContent =  qGuestbook.content.contains(keyword);

        // exTitle과 exContent라는 Boolean Expression을 결합하는 부분
        BooleanExpression exAll = exTitle.or(exContent);

        // BooleanBuilder에 추가
        builder.and(exAll);

        // 3. gno가 0보다 크다는 조건 추가
        builder.and(qGuestbook.gno.gt(0L)); // 3-----------

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });

    }

}
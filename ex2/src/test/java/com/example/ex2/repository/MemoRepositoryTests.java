package com.example.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.ex2.entity.Memo;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Commit;

import java.util.List;

@SpringBootTest
public class MemoRepositoryTests {
    @Autowired
    MemoRepository memoRepository;

    @Test
    public void testClass() {
        System.out.println(memoRepository.getClass().getName());
    }

    /* CRUD */

    // CREATE
    // 100개의 새로운 Memo 객체를 생성하고 MemoRepository를 이용해서 이를 insert
    @Test
    public void testInsertDummies() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Memo memo = Memo.builder().memoText("Sample..."+i).build();
            memoRepository.save(memo);
        });
    }

    // READ
    // findById()나 getOne() 등을 이용해서 엔티티 객체 조회
    // findById()는 실행한 순간에 이미 SQL을 처리, getOne()은 실제 객체를 사용하는 순간에 SQL 동작
    @Test
    public void testSelect() {
        Long mno = 100L; // 데이터 베이스에 존재하는 mno
        Optional<Memo> result = memoRepository.findById(mno); // 이 부분에서 바로 SQL이 동작하므로 ===가 나중에 나온다.

        System.out.println("==============================");

        if (result.isPresent()) {
            Memo memo = result.get();
            System.out.println(memo);
        }
    }

    // getOne의 경우에 Transactional 추가로 필요
//    @Transactional
//    @Test
//    public void testSelect2() {
//        Long mno = 100L;
//        Memo memo = memoRepository.getOne(mno);
//
//        System.out.println("==============================");
//
//        System.out.println(memo); // 이 부분에서 SQL이 동작하므로 위의 ===가 먼저 나온다.
//    }

    // UPDATE
    @Test
    public void testUpdate() {
        // 내부적으로 select 쿼리로 해당 번호의 Memo 객체를 확인하고 이를 update 한다.
        // JPA는 엔티티 객체들을 메모리상에 보관하려고 하기 때문에
        // 특정한 엔티티 객체가 존재하는지 확인하는 select가 먼저 실행되고
        // 해당 @Id를 가진 객체가 있다면 Update, 그렇지 않다면 insert를 실행한다.
        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();
        System.out.println(memoRepository.save(memo));
    }

    // DELETE
    @Test
    public void testDelete() {
        // 마찬가지로 삭제하려는 번호의 엔티티 객체가 있는지 먼저 확인하고 삭제한다.
        // deleteByID()의 리턴 타입은 void이고 해당 데이터가 존재하지 않으면
        // org.springframework.dao.EmptyResultDataAccessException 예외를 발생시킨다.
        Long mno = 100L;
        memoRepository.deleteById(mno);
    }


    /* Paging */

    // 페이징 처리
    @Test
    public void testPageDefault() {
        Pageable pageable = PageRequest.of(0, 10); // 1페이지의 데이터 10개
        Page<Memo> result = memoRepository.findAll(pageable);
        System.out.println(result);

        System.out.println("------------------------------");

        System.out.println("Total Page: " + result.getTotalPages()); // 총 몇 페이지
        System.out.println("Total Count: " + result.getTotalElements()); // 전체
        System.out.println("Page Number: " + result.getNumber()); // 현재 페이지 번호 0부터 시작
        System.out.println("Page Size: " + result.getSize()); // 페이지당 데이터 개수
        System.out.println("has next page?: " + result.hasNext()); // 다음 페이지 존재 여부
        System.out.println("first page?: " + result.isFirst()); // 시작 페이지 여부

        System.out.println("------------------------------");

        for (Memo memo : result.getContent()) {
            System.out.println(memo);
        }
    }

    // 정렬 조건 추가하기
    @Test
    public void testSort() {
        Sort sort1 = Sort.by("mno").descending(); // mno 필드의 값을 역순으로 정렬
        Sort sort2 = Sort.by("memoText").ascending(); // memoText 필드의 값을 오름차순으로 정렬
        Sort sortAll = sort1.and(sort2);
        Pageable pageable = PageRequest.of(0, 10, sortAll);
        Page<Memo> result = memoRepository.findAll(pageable);
        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }


    /* Query 메서드 */

    @Test
    public void testQueryMethods() {
        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);
        for (Memo memo : list) {
            System.out.println(memo);
        }
    }

    // Query 메서드와 Pageable 결합
    // 정렬 조건을 Pageable을 통해서 조절할 수 있기 때문에 간단한 형태의 메서드 선언 가능
    @Test
    public void testQueryMethodWithPageable() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
        Page<Memo> result = memoRepository.findByMnoBetween(10L, 50L, pageable);
        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }

    // delete 작업 수행 시 select문으로 해당 엔티티 객체들을 가져오는 작업과
    // 각 엔티티를 삭제하는 작업이 같이 이루어지기 때문에 @Transactional과 @Commit을 사용
    // @Commit이 없다면 deleteBy()는 롤백 처리되어 반영되지 않음
    // 실제 개발에서는 한 번에 삭제가 이루어지는 경우가 흔하지 않으므로 잘 쓰이지 않음
    @Commit
    @Transactional
    @Test
    public void testDeleteQueryMethods() {
        memoRepository.deleteMemoByMnoLessThan(10L);
    }


    /* @Query 어노테이션 */
    // 메서드의 이름과 상관없이 메서드에 추가한 어노테이션을 통해서 원하는 처리가 가능
    // @Query("select m from Memo m order by m.mno desc")
    // List<Memo> getListDesc();

    // 파라미터 바인딩에는 파라미터의 순서를 이용하거나 파라미터의 이름, 자바 빈 스타일을 이용하는 방식 등이 있음
    // 예) 파라미터를 이용하는 방식
    // @Transactional
    // @Modifying
    // @Query("update Memo m set m.memoText = :memoText where m.mno = :mno")
    // int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);

    // NativeSQL 처리
    // JPA 자체가 데이터베이스에 독립적으로 구현이 가능하다는 장점을 잃어버리기는 하지만
    // 경우에 따라서 복잡한 JOIN 구문 등을 처리하기 위해서 어쩔 수 없는 선택을 하는 경우에 사용
    // @Query(value = "select * from memo where mno > 0", nativeQuery = true)s
    // List<Object[]> getNativeResult();
}

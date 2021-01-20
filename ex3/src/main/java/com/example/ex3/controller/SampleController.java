package com.example.ex3.controller;

import com.example.ex3.dto.SampleDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/sample")
@Log4j2 // 동작을 확인하기 위함
public class SampleController {

    @GetMapping("/ex1")
    public void ex1() {
        log.info("ex1..........");
    }

//    @GetMapping("/ex2")
//    public void exModel(Model model) {
//        // SampleDTO 타입 객체를 20개 추가하고 이를 Model에 담아서 전송
//        // @GetMapping의 value 속성값을 '{}'로 처리하면 하나 이상의 URL 지정 가능
//        List<SampleDTO> list = IntStream.rangeClosed(1, 20).asLongStream().mapToObj(i -> {
//            SampleDTO dto = SampleDTO.builder()
//                    .sno(i)
//                    .first("First..." + i)
//                    .last("Last..."+i)
//                    .regTime(LocalDateTime.now())
//                    .build();
//            return dto;
//        }).collect(Collectors.toList());
//
//        model.addAttribute("list", list);
//    }

    @GetMapping({"/exInline"})
    public String exInline(RedirectAttributes redirectAttributes){

        log.info("exInline..............");

        SampleDTO dto = SampleDTO.builder()
                .sno(100L)
                .first("First..100")
                .last("Last..100")
                .regTime(LocalDateTime.now())
                .build();
        redirectAttributes.addFlashAttribute("result", "success");
        redirectAttributes.addFlashAttribute("dto", dto);


        return "redirect:/sample/ex3";
    }

    @GetMapping("/ex3")
    public void ex3(){

        log.info("ex3");
    }

    // Thymeleaf의 링크는 @{}를 이용
    // 파라미터를 전달하는 상황에서 좀 더 가독성 좋은 코드를 만들 수 있다.
    @GetMapping({"/ex2", "/exLink"})
    public void exModel(Model model){

        List<SampleDTO> list = IntStream.rangeClosed(1,20).asLongStream().mapToObj(i -> {
            SampleDTO dto = SampleDTO.builder()
                    .sno(i)
                    .first("First.."+i)
                    .last("Last.."+i)
                    .regTime(LocalDateTime.now())
                    .build();
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("list", list);
    }

//    @GetMapping("/exLayout1")
//    public void exLayout1(){
//
//        log.info("exLayout............");
//    }

    @GetMapping({"/exLayout1","/exLayout2", "/exTemplate","/exSidebar"})
    public void exLayout1() {
        log.info("exLayout............");
    }
}

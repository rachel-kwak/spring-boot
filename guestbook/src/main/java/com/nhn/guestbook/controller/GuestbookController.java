package com.nhn.guestbook.controller;

import com.nhn.guestbook.dto.GuestbookDTO;
import com.nhn.guestbook.dto.PageRequestDTO;
import com.nhn.guestbook.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor // 자동 주입
public class GuestbookController {
    private final GuestbookService service; //final로 선언

    @GetMapping("/")
    public String index() {
        return "redirect:/guestbook/list";
    }

    // 스프링 MVC는 파라미터를 자동으로 수집해주는 기능이 있으므로
    // 화면에서 page와 size라는 파라미터를 전달하면 PageRequestDTO 객체로 자동 수집
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        log.info("list............." + pageRequestDTO);
        // GuestbookServiceImpl에서 반환하는 PageResultDTO를 'result'라는 이름으로 전달
        model.addAttribute("result", service.getList(pageRequestDTO));
    }

    // 화면을 보여준다.
    @GetMapping("/register")
    public void register() {
        log.info("register get...");
    }

    // 처리 후에 목록 페이지로 이동한다.
    @PostMapping("/register")
    public String registerPost(GuestbookDTO dto, RedirectAttributes redirectAttributes) {
        log.info("dto..." + dto);

        // 새로 추가된 엔티티의 번호
        Long gno = service.register(dto);
        redirectAttributes.addFlashAttribute("msg", gno);   // 단 한 번만 데이터를 전달하는 용도

        return "redirect:/guestbook/list";
    }

    // Get 방식으로 gno 값을 받아서 Model에 GuestbookDTO 객체를 담아서 전달
    // 나중에 다시 목록 페이지로 돌아가기 위해서 PageRequestDTO도 같이 저장
    // @GetMapping("/read")
    @GetMapping({"/read", "/modify"})
    public void read(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {
        log.info("gno: " + gno);
        GuestbookDTO dto = service.read(gno);
        model.addAttribute("dto", dto);
    }

    // 방명록의 삭제는 POST 방식으로 gno 값을 전달하고,
    // 삭제 후에는 다시 목록의 첫 페이지로 이동하는 방식
    @PostMapping("/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes) {
        log.info("gno: " + gno);

        service.remove(gno);
        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/guestbook/list";
    }

    @PostMapping("/modify")
    public String modify(GuestbookDTO dto,  // 수정해야하는 글의 정보를 가짐
                         @ModelAttribute("requestDTO") PageRequestDTO requestDTO,   // 기존의 페이지 정보를 유지하기 위함
                         RedirectAttributes redirectAttributes) {   // 리다이렉트로 이동하기 위함
        log.info("post modify........");
        log.info("dto: " + dto);
        service.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("gno", dto.getGno());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());

        return "redirect:/guestbook/read";
    }
}

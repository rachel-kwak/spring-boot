package com.example.ex1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// RestController를 이용해서 별도의 화면없이 데이터를 전송
// localhost:8080/hello를 호출하면 문자열 리턴

@RestController
public class SampleController {

    @GetMapping("/hello")
    public String[] hello() {
        return new String[]{"Hello", "World"};
    }
}

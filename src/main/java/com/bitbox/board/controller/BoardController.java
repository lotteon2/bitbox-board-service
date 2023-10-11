package com.bitbox.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BoardController {
    @GetMapping
    public String test(){
        return "testBoard";
    }
}
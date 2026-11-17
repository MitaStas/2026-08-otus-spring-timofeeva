package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @GetMapping("/")
    public String listPage() {
        return "listBooks";
    }

    @GetMapping("/books")
    public String editPage() {
        return "editBook";
    }

}

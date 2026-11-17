package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.CommentService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public String addComment(@RequestParam long bookId, @RequestParam String text) {
        commentService.addComments(bookId, Set.of(text));
        return "redirect:/books?id=" + bookId;
    }

    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable long id, @RequestParam long bookId) {
        commentService.deleteById(id);
        return "redirect:/books?id=" + bookId;
    }
}

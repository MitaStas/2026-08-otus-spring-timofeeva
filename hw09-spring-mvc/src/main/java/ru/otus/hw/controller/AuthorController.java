package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public String listPage(Model model) {
        List<AuthorDto> authors = authorService.findAll().stream()
                .map(AuthorDto::fromDomainObject).toList();
        model.addAttribute("authors", authors);
        return "listAuthors";
    }

    @GetMapping("/authors/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        var author = authorService.findById(id)
                .map(AuthorDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
        model.addAttribute("author", author);
        return "editAuthor.html";
    }

    @PostMapping("/authors")
    public String add(@RequestParam String fullName) {
        authorService.insert(fullName);
        return "redirect:/authors";
    }

    @DeleteMapping("/authors/{id}")
    public String delete(@PathVariable long id) {
        authorService.deleteById(id);
        return "redirect:/authors";
    }
}

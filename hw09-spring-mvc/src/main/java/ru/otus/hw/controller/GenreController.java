package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public String listPage(Model model) {
        model.addAttribute("genres", genreService.findAll().stream()
                .map(GenreDto::fromDomainObject).toList());
        return "listGenres";
    }

    @GetMapping("/genres/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        var genre = genreService.findById(id)
                .map(GenreDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
        model.addAttribute("genre", genre);
        return "editGenre";
    }

    @PostMapping("/genres")
    public String add(@RequestParam String name) {
        genreService.insert(name);
        return "redirect:/genres";
    }

    @DeleteMapping("/genres/{id}")
    public String delete(@PathVariable long id) {
        genreService.deleteById(id);
        return "redirect:/genres";
    }
}

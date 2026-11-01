package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<BookDto> books = bookService.findAll().stream()
                .map(BookDto::fromDomainObject)
                .toList();
        model.addAttribute("books", books);
        return "listBooks";
    }

    @GetMapping("/books")
    public String editPage(@RequestParam(required = false) Long id, Model model) {
        BookDto book = id == null ? new BookDto(0, "", null, List.of(), List.of()) : bookService.findById(id)
                .map(domainBook -> BookDto.fromDomainObject(domainBook,
                        commentService.findByBookId(domainBook.getId())))
                .orElseThrow(NotFoundException::new);
        model.addAttribute("book", book);
        addBookFormAttributes(model);
        return "editBook";
    }

    @PostMapping("/books")
    public String add(@Valid @ModelAttribute("book") BookDto book,
                      BindingResult bindingResult,
                      @RequestParam(required = false) String authorId,
                      @RequestParam(required = false) List<String> genreIds,
                      Model model) {
        Long parsedAuthorId =  parseAuthorId(authorId, bindingResult);
        Set<Long> parsedGenreIds = parseGenreIds(genreIds, bindingResult);
        if (bindingResult.hasErrors()) {
            addBookFormAttributes(model);
            return "editBook";
        }
        try {
            bookService.update(book.getId(), book.getTitle(), parsedAuthorId, parsedGenreIds);
        } catch (EntityNotFoundException | IllegalArgumentException ex) {
            log.warn("Unable to save book: {}", ex.getMessage());
            bindingResult.reject("book.save", ex.getMessage());
            addBookFormAttributes(model);
            return "editBook";
        }
        return "redirect:/";
    }

    @DeleteMapping("/books")
    public String deleteBook(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    private void addBookFormAttributes(Model model) {
        model.addAttribute("authors", authorService.findAll().stream()
                .map(AuthorDto::fromDomainObject).toList());
        model.addAttribute("genres", genreService.findAll().stream()
                .map(GenreDto::fromDomainObject).toList());
    }

    private Long parseAuthorId(String authorId, BindingResult bindingResult) {
        if (authorId == null) {
            bindingResult.reject("book.author", "Выберите автора");
            return null;
        }
        try {
            return Long.valueOf(authorId);
        } catch (NumberFormatException ex) {
            bindingResult.reject("book.author", "Выберите корректного автора");
            return null;
        }
    }

    private Set<Long> parseGenreIds(List<String> genreIds, BindingResult bindingResult) {
        if (genreIds == null || genreIds.isEmpty()) {
            bindingResult.reject("book.genres", "Выберите хотя бы один жанр");
            return Set.of();
        }
        try {
            return genreIds.stream().map(Long::valueOf).collect(java.util.stream.Collectors.toSet());
        } catch (NumberFormatException ex) {
            bindingResult.reject("book.genres", "Выберите корректные жанры");
            return Set.of();
        }
    }
}

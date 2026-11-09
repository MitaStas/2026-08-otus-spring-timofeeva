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
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

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
        model.addAttribute("books", bookService.findAll());
        return "listBooks";
    }

    @GetMapping("/books")
    public String editPage(@RequestParam(required = false) Long id, Model model) {
        BookDto book = id == null ? new BookDto(0, "", null, List.of(), List.of()) : bookService.findById(id);
        if (id != null) {
            book.setComments(commentService.findByBookId(book.getId()).stream()
                    .map(CommentDto::fromDomainObject)
                    .toList());
        }
        model.addAttribute("book", book);
        model.addAttribute("bookRequest", id == null
                ? new BookRequestDto(0, "", null, List.of())
                : BookRequestDto.fromBookDto(book));
        addBookFormAttributes(model);
        return "editBook";
    }

    @PostMapping("/books")
    public String add(@Valid @ModelAttribute("bookRequest") BookRequestDto bookRequest,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            addBookForFormError(model, bookRequest);
            addBookFormAttributes(model);
            return "editBook";
        }
        try {
            BookDto book = bookRequest.toBookDto();
            if (bookRequest.getId() == 0) {
                bookService.insert(book);
            } else {
                bookService.update(book);
            }
        } catch (EntityNotFoundException | IllegalArgumentException ex) {
            log.warn("Unable to save book: {}", ex.getMessage());
            bindingResult.reject("book.save", ex.getMessage());
            addBookForFormError(model, bookRequest);
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

    private void addBookForFormError(Model model, BookRequestDto bookRequest) {
        BookDto book = new BookDto(bookRequest.getId(), bookRequest.getTitle(), null, List.of(), List.of());
        if (bookRequest.getId() != 0) {
            book.setComments(commentService.findByBookId(bookRequest.getId()).stream()
                    .map(CommentDto::fromDomainObject)
                    .toList());
        }
        model.addAttribute("book", book);
    }
}

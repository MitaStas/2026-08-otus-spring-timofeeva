package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.Set;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final BookConverter bookConverter;

    //cid 1
    @ShellMethod(value = "Find comment by id", key = "cid")
    public String findById(String id) {
        return commentService.findById(id)
                .map(Comment::toString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    //acom
    @ShellMethod(value = "Find all comments", key = "acom")
    public String findAllComments() {
        return commentService.findAll().toString();
    }

    //cbid 1
    @ShellMethod(value = "Find comment by bookId", key = "cbid")
    public String findByBookId(String bookId) {
        return commentService.findByBookId(bookId)
                .toString();
    }

    // bacs 1 комментарий1,комментарий2
    @ShellMethod(value = "Add comment to book", key = "bacs")
    public String addNewComments(String bookId, Set<String> comments) {
        var book = commentService.addComments(bookId, comments);
        return "Комментарии добавлены";
    }

    // cupd 1 editedComment
    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String text) {
        var savedComment = commentService.update(id, text);
        return savedComment.toString();
    }

    // cdel 1
    @ShellMethod(value = "Delete comment", key = "cdel")
    public String updateComment(String id) {
        commentService.deleteById(id);
        return "Удалена запись с id: " + id;
    }
}

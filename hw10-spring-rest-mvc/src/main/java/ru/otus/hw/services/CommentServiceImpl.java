package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public CommentDto findById(long id) {
        return commentRepository.findById(id)
                .map(CommentDto::fromDomainObject)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public List<CommentDto> findAll() {
        return commentRepository.findAll().stream()
                .map(CommentDto::fromDomainObject)
                .toList();
    }

    @Override
    @Transactional
    public List<CommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(CommentDto::fromDomainObject)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(long bookId, String commentText) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        if (commentText == null || commentText.isEmpty()) {
            throw new EntityNotFoundException("Comment can not be empty");
        }
        var comment = new Comment().setText(commentText);
        book.addComment(comment);
        return CommentDto.fromDomainObject(commentRepository.save(comment));

    }

    @Override
    @Transactional
    public CommentDto update(long id, String text) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        comment.setText(text);
        return CommentDto.fromDomainObject(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}

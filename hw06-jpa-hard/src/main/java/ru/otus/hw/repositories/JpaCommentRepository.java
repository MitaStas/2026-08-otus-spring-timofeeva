package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Comment> findById(long id) {
        return entityManager.createQuery("""
                        select c
                        from Comment c
                        join fetch c.book
                        where c.id = :id
                        """, Comment.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return entityManager.createQuery("""
                        select c
                        from Comment c
                        join fetch c.book
                        where c.book.id = :bookId
                        """, Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public int deleteById(long id) {
        return entityManager.createQuery("delete from Comment c where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

}

package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        var query = entityManager.createQuery(
                """
                        select b
                        from Book b
                        join fetch b.author
                        join fetch b.genres
                        where b.id = :id
                        """, Book.class
        );
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery(
                """
                        select distinct b
                        from Book b
                        join fetch b.author
                        join fetch b.genres
                        """, Book.class
        ).getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Query query = entityManager.createQuery("delete from Book b where id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const booksList = document.getElementById('books-list');
    if (booksList !== null) {
        booksList.addEventListener('click', deleteBook);
        loadBooks();
    }

    if (document.getElementById('edit-form') !== null) {
        loadEditBookPage();
    }
});

async function loadBooks() {
    const booksList = document.getElementById('books-list');
    const template = document.getElementById('book-row-template');
    const response = await fetch('/api/books');
    const books = await response.json();

    booksList.replaceChildren();
    books.forEach((book) => {
        const bookRow = template.content.cloneNode(true);
        bookRow.querySelector('[data-book-id]').textContent = book.id;
        bookRow.querySelector('[data-book-title]').textContent = book.title;
        bookRow.querySelector('[data-book-author]').textContent = book.author.fullName;
        bookRow.querySelector('[data-book-genres]').textContent = book.genres
            .map((genre) => genre.name).join(', ');
        bookRow.querySelector('[data-edit-book]').href = `/books?id=${book.id}`;
        bookRow.querySelector('[data-delete-book]').dataset.bookId = book.id;
        booksList.append(bookRow);
    });
}

async function deleteBook(event) {
    const bookId = event.target.dataset.bookId;
    if (bookId === undefined) {
        return;
    }
    const response = await fetch(`/api/books/${bookId}`, {method: 'DELETE'});
    if (response.ok) {
        loadBooks();
    }
}

async function loadEditBookPage() {
    const bookId = new URLSearchParams(window.location.search).get('id');
    const book = bookId === null ? null : await getBook(bookId);
    if (bookId !== null && book === null) {
        return;
    }

    const [authors, genres, comments] = await Promise.all([
        getJson('/api/authors'),
        getJson('/api/genres'),
        bookId === null ? null : getJson(`/api/books/${bookId}/comments`)
    ]);
    authors.forEach((author) => document.getElementById('book-author-select')
        .append(new Option(author.fullName, author.id)));
    genres.forEach((genre) => document.getElementById('book-genre-select')
        .append(new Option(genre.name, genre.id)));
    if (book !== null) {
        showBook(book);
        showComments(comments, book.id);
    }
    document.getElementById('edit-form').addEventListener('submit', saveBook);
    document.getElementById('comment-form').addEventListener('submit', addComment);
}

async function getJson(url) {
    const response = await fetch(url);
    return response.json();
}

async function getBook(bookId) {
    const response = await fetch(`/api/books/${bookId}`);
    if (response.ok) {
        return response.json();
    }

    document.open();
    document.write(await response.text());
    document.close();
    return null;
}

function showBook(book) {
    document.getElementById('id-input').value = book.id;
    document.getElementById('book-title-input').value = book.title;
    document.getElementById('book-author-select').value = book.author.id;
    const genreIds = book.genres.map((genre) => String(genre.id));
    Array.from(document.getElementById('book-genre-select').options)
        .forEach((option) => option.selected = genreIds.includes(option.value));
}

function showComments(comments, bookId) {
    const commentsList = document.getElementById('comments-list');
    const template = document.getElementById('comment-template');

    commentsList.replaceChildren();
    comments.forEach((comment) => {
        const commentElement = template.content.cloneNode(true);
        commentElement.querySelector('[data-comment-text]').textContent = comment.text;
        commentElement.querySelector('[data-delete-comment]').dataset.commentId = comment.id;
        commentsList.append(commentElement);
    });
    document.getElementById('comment-book-id').value = bookId;
    document.getElementById('comments-section').hidden = false;
}

async function saveBook(event) {
    event.preventDefault();
    clearBookErrors();
    const id = Number(document.getElementById('id-input').value);
    const authorSelect = document.getElementById('book-author-select');
    const book = {
        title: document.getElementById('book-title-input').value,
        authorId: authorSelect.value === '' ? null : Number(authorSelect.value),
        genreIds: Array.from(document.getElementById('book-genre-select').selectedOptions,
            (option) => Number(option.value))
    };
    const url = id === 0 ? '/api/books' : `/api/books/${id}`;
    const method = id === 0 ? 'POST' : 'PUT';
    const response = await fetch(url, {
        method: method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(book)
    });
    if (!response.ok) {
        showBookErrors(await response.json());
        return;
    }
    window.location.href = '/';
}

function clearBookErrors() {
    document.querySelectorAll('#edit-form .errors').forEach((errorElement) => {
        errorElement.textContent = '';
        errorElement.hidden = true;
    });
}

function showBookErrors(errors) {
    Object.entries(errors).forEach(([field, message]) => {
        const errorElement = document.getElementById(`${field}-error`)
            || document.getElementById(`${field.split('[')[0]}-error`);
        if (errorElement !== null) {
            errorElement.textContent = message;
            errorElement.hidden = false;
        }
    });
}

async function addComment(event) {
    event.preventDefault();
    const bookId = document.getElementById('comment-book-id').value;
    const textInput = document.getElementById('comment-text-input');
    const response = await fetch(`/api/books/${bookId}/comments`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({text: textInput.value})
    });
    if (!response.ok) {
        return;
    }
    textInput.value = '';
    showComments(await getJson(`/api/books/${bookId}/comments`), bookId);
}

async function deleteComment(button) {
    const commentId = button.dataset.commentId;
    const bookId = document.getElementById('comment-book-id').value;
    const response = await fetch(`/api/books/${bookId}/comments/${commentId}`, {method: 'DELETE'});
    if (!response.ok) {
        return;
    }
    showComments(await getJson(`/api/books/${bookId}/comments`), bookId);
}

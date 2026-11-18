document.addEventListener('DOMContentLoaded', loadAuthors);

async function loadAuthors() {
    const authorsList = document.getElementById('authors-list');
    const template = document.getElementById('author-row-template');
    const response = await fetch('/api/authors');
    const authors = await response.json();

    authorsList.replaceChildren();
    authors.forEach((author) => {
        const authorRow = template.content.cloneNode(true);
        authorRow.querySelector('[data-author-id]').textContent = author.id;
        authorRow.querySelector('[data-author-full-name]').value = author.fullName;
        authorRow.querySelector('[data-update-author]').dataset.authorId = author.id;
        authorRow.querySelector('[data-delete-author]').dataset.authorId = author.id;
        authorsList.append(authorRow);
    });
}

async function createAuthor(event) {
    event.preventDefault();
    clearAuthorError();

    const input = document.getElementById('author-full-name-input');
    const response = await fetch('/api/authors', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({fullName: input.value})
    });
    if (!response.ok) {
        showAuthorError(await response.json());
        return;
    }

    input.value = '';
    loadAuthors();
}

async function updateAuthor(button) {
    clearAuthorError();
    const authorId = button.dataset.authorId;
    const fullName = button.closest('tr').querySelector('[data-author-full-name]').value;
    const response = await fetch(`/api/authors/${authorId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({fullName: fullName})
    });
    if (!response.ok) {
        showAuthorError(await response.json());
        return;
    }

    loadAuthors();
}

async function deleteAuthor(button) {
    clearAuthorError();
    const response = await fetch(`/api/authors/${button.dataset.authorId}`, {method: 'DELETE'});
    if (!response.ok) {
        showAuthorError(await response.json());
        return;
    }

    loadAuthors();
}

function clearAuthorError() {
    const errorElement = document.getElementById('author-error');
    errorElement.textContent = '';
    errorElement.hidden = true;
}

function showAuthorError(errors) {
    const errorElement = document.getElementById('author-error');
    errorElement.textContent = errors.fullName || errors.error;
    errorElement.hidden = false;
}

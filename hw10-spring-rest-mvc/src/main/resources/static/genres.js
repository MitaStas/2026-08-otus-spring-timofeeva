document.addEventListener('DOMContentLoaded', loadGenres);

async function loadGenres() {
    const genresList = document.getElementById('genres-list');
    const template = document.getElementById('genre-row-template');
    const response = await fetch('/api/genres');
    const genres = await response.json();

    genresList.replaceChildren();
    genres.forEach((genre) => {
        const genreRow = template.content.cloneNode(true);
        genreRow.querySelector('[data-genre-id]').textContent = genre.id;
        genreRow.querySelector('[data-genre-name]').value = genre.name;
        genreRow.querySelector('[data-update-genre]').dataset.genreId = genre.id;
        genreRow.querySelector('[data-delete-genre]').dataset.genreId = genre.id;
        genresList.append(genreRow);
    });
}

async function createGenre(event) {
    event.preventDefault();
    clearGenreError();

    const input = document.getElementById('genre-name-input');
    const response = await fetch('/api/genres', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: input.value})
    });
    if (!response.ok) {
        showGenreError(await response.json());
        return;
    }

    input.value = '';
    loadGenres();
}

async function updateGenre(button) {
    clearGenreError();
    const genreId = button.dataset.genreId;
    const name = button.closest('tr').querySelector('[data-genre-name]').value;
    const response = await fetch(`/api/genres/${genreId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name})
    });
    if (!response.ok) {
        showGenreError(await response.json());
        return;
    }

    loadGenres();
}

async function deleteGenre(button) {
    clearGenreError();
    const response = await fetch(`/api/genres/${button.dataset.genreId}`, {method: 'DELETE'});
    if (!response.ok) {
        showGenreError(await response.json());
        return;
    }

    loadGenres();
}

function clearGenreError() {
    const errorElement = document.getElementById('genre-error');
    errorElement.textContent = '';
    errorElement.hidden = true;
}

function showGenreError(errors) {
    const errorElement = document.getElementById('genre-error');
    errorElement.textContent = errors.name || errors.error;
    errorElement.hidden = false;
}

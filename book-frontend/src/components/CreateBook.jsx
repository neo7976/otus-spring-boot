import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function CreateBook() {
    const [title, setTitle] = useState('');
    const [authorId, setAuthorId] = useState('');
    const [genreId, setGenreId] = useState('');
    const [authors, setAuthors] = useState([]);
    const [genres, setGenres] = useState([]);
    const [message, setMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();

    // Загружаем авторов и жанры при старте
    useEffect(() => {
        const loadData = async () => {
            try {
                const [authorRes, genreRes] = await Promise.all([
                    api.get('/api/authors'),   // ← вам нужно добавить эти эндпоинты в бэкенд
                    api.get('/api/genres')
                ]);
                setAuthors(authorRes.data);
                setGenres(genreRes.data);
            } catch (err) {
                setMessage({ type: 'error', text: 'Не удалось загрузить данные' });
            }
        };
        loadData();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage({ type: '', text: '' });

        const bookDto = {
            title,
            author: { authorId: parseInt(authorId) },
            genre: { genreId: parseInt(genreId) }
        };

        try {
            await api.post('/book/api/v1', bookDto);
            setMessage({ type: 'success', text: 'Книга успешно создана!' });
            setTimeout(() => navigate('/'), 1500);
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка создания книги' });
        }
    };

    return (
        <div className="container">
            <h1>📖 Добавить новую книгу</h1>

            {message.text && (
                <div className={`message-box ${message.type}`}>
                    {message.text}
                </div>
            )}

            <div className="form-card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="title">Название книги:</label>
                        <input
                            id="title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                            placeholder="Введите название"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="authorId">Автор:</label>
                        <select
                            id="authorId"
                            value={authorId}
                            onChange={(e) => setAuthorId(e.target.value)}
                            required
                        >
                            <option value="">-- Выберите автора --</option>
                            {authors.map(author => (
                                <option key={author.authorId} value={author.authorId}>
                                    {author.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="genreId">Жанр:</label>
                        <select
                            id="genreId"
                            value={genreId}
                            onChange={(e) => setGenreId(e.target.value)}
                            required
                        >
                            <option value="">-- Выберите жанр --</option>
                            {genres.map(genre => (
                                <option key={genre.genreId} value={genre.genreId}>
                                    {genre.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="btn btn-primary">
                            ✅ Создать книгу
                        </button>
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/')}>
                            ← Назад к списку
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
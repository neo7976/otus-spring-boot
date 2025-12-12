import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

export default function BookList() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadBooks();
    }, []);

    const loadBooks = async () => {
        try {
            const res = await api.get('/book/api/v1');
            setBooks(res.data.data || []);
        } catch (err) {
            setError('Не удалось загрузить книги');
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Загрузка...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div>
            <h1>📚 Список книг</h1>
            <table>
                <thead>
                <tr><th>ID</th><th>Название</th><th>Автор</th><th>Жанр</th><th>Действия</th></tr>
                </thead>
                <tbody>
                {books.map(book => (
                    <tr key={book.bookId}>
                        <td>{book.bookId}</td>
                        <td>{book.title}</td>
                        <td>{book.author?.name}</td>
                        <td>{book.genre?.name}</td>
                        <td>
                            <Link to={`/book/${book.bookId}/comment`}>Добавить комментарий</Link>
                            {/*<button onClick={() => showComments(book.bookId)}>Комментарии</button>*/}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <Link to="/book/create">➕ Добавить книгу</Link>
        </div>
    );
}
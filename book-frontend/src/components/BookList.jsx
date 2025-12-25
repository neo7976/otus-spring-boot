import React, {useState, useEffect} from 'react';
import {Link} from 'react-router-dom';
import api from '../services/api';
import '../styles/book-list.css';

export default function BookList() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [modalOpen, setModalOpen] = useState(false);
    const [selectedBookId, setSelectedBookId] = useState(null);
    const [comments, setComments] = useState([]);
    const [loadingComments, setLoadingComments] = useState(false);
    const [commentsError, setCommentsError] = useState('');

    useEffect(() => {
        loadBooks();
    }, []);

    const loadBooks = async () => {
        try {
            const res = await api.get('/book/api/v1');
            // Уточните структуру: если res.data.data — ок, иначе res.data.value
            setBooks(res.data.data || []);
        } catch (err) {
            setError('Не удалось загрузить книги');
        } finally {
            setLoading(false);
        }
    };

    const openCommentsModal = async (bookId) => {
        setSelectedBookId(bookId);
        setLoadingComments(true);
        setCommentsError('');
        setComments([]);

        try {
            // Запрос книги по ID — в ответе уже есть комментарии
            const res = await api.get(`/book/api/v1/${bookId}`);
            const book = res.data.value; // ← именно так, согласно вашему примеру

            if (book && Array.isArray(book.comments)) {
                setComments(book.comments);
            } else {
                setComments([]);
            }
        } catch (err) {
            console.error('Ошибка загрузки комментариев:', err);
            setCommentsError('Не удалось загрузить комментарии');
            setComments([]);
        } finally {
            setLoadingComments(false);
            setModalOpen(true); // ← модалка открывается ВСЕГДА после запроса
        }
    };

    const closeCommentsModal = () => {
        setModalOpen(false);
        setSelectedBookId(null);
        setComments([]);
        setCommentsError('');
    };

    if (loading) return <div className="loading">Загрузка...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div className="container">
            <h1>📚 Список книг</h1>
            <div className="table-container">
                <table id="booksTable">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Автор</th>
                        <th>Жанр</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {books.map(book => (
                        <tr key={book.bookId}>
                            <td>{book.bookId}</td>
                            <td>{book.title}</td>
                            <td>{book.author?.name}</td>
                            <td>{book.genre?.name}</td>
                            <td>
                                <button
                                    type="button"
                                    onClick={() => openCommentsModal(book.bookId)}
                                    className="btn btn-outline-primary btn-sm"
                                >
                                    Комментарии
                                </button>
                                &nbsp;
                                <Link
                                    to={`/book/${book.bookId}/comment`}
                                    className="btn btn-outline-info btn-sm"
                                >
                                    Добавить
                                </Link>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <Link to="/book/create" className="btn btn-primary">
                ➕ Добавить книгу
            </Link>

            {/* Модальное окно */}
            {modalOpen && (
                <>
                    <div className="modal" onClick={closeCommentsModal}>
                        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                            <span className="close" onClick={closeCommentsModal}>&times;</span>
                            <h3>Комментарии к книге #{selectedBookId}</h3>

                            {loadingComments ? (
                                <div className="loading">Загрузка...</div>
                            ) : commentsError ? (
                                <div className="error">{commentsError}</div>
                            ) : comments.length > 0 ? (
                                <ul className="comment-items">
                                    {comments.map(comment => (
                                        <li key={comment.commentId} className="comment-item">
                                            <div className="comment-header">
                                                <span>{comment.user?.username || 'Аноним'}</span>
                                                <span>
                                                {new Date(comment.createAt).toLocaleDateString('ru-RU')}
                                            </span>
                                            </div>
                                            <div className="comment-text">{comment.text}</div>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="no-comments">Нет комментариев</p>
                            )}
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}
import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';

export default function BookView() {
    const { id } = useParams(); // из URL: /book/5
    const [commentText, setCommentText] = useState('');
    const [book, setBook] = useState(null);
    const [comments, setComments] = useState([]);
    const [rating, setRating] = useState(null);
    const [message, setMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();

    // Загружаем комментарии при старте
    useEffect(() => {
        loadComments();
    }, [id]);

    const loadComments = async () => {
        try {
            const res = await api.get(`/book/api/v1/${id}`);
            setBook(res.data.value);
            setComments(res.data.value.comments || []);
            // Рейтинг book-service получает из rating-service через Feign
            setRating(res.data.value.rating);
        } catch (err) {
            setMessage({ type: 'error', text: 'Не удалось загрузить книгу' });
        }
    };

    const handleRate = async (score) => {
        try {
            const res = await api.post(`/book/api/v1/${id}/rating`, { score });
            setRating(res.data.value);
            setMessage({ type: 'success', text: `✅ Ваша оценка ${score} сохранена` });
            setTimeout(() => setMessage({ type: '', text: '' }), 2000);
        } catch (err) {
            // 503 — rating-service недоступен, сработал fallback
            setMessage({ type: 'error', text: '❌ ' + (err.response?.data?.msg || 'Не удалось сохранить оценку') });
        }
    };

    const renderRating = () => {
        if (!rating || !rating.available) {
            return <span className="rating-unavailable">временно недоступен</span>;
        }
        if (rating.count === 0) {
            return <span>оценок пока нет</span>;
        }
        return <span><strong>{rating.average}</strong> ★ (оценок: {rating.count})</span>;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!commentText.trim()) return;

        try {
            await api.post(`/comment/api/v1/add-comment/${id}`, { text: commentText });
            setCommentText('');
            setMessage({ type: 'success', text: '✅ Комментарий добавлен!' });
            loadComments(); // обновить список
            setTimeout(() => setMessage({ type: '', text: '' }), 2000);
        } catch (err) {
            setMessage({ type: 'error', text: '❌ Ошибка при добавлении комментария' });
        }
    };

    return (
        <div className="container">
            <h1>📖 {book ? book.title : 'Загрузка...'}</h1>
            {book && (
                <p className="book-meta">
                    Автор: <strong>{book.author?.name || '—'}</strong>
                    {' · '}
                    Жанр: <strong>{book.genre?.name || '—'}</strong>
                </p>
            )}

            {message.text && (
                <div className={`message-box ${message.type}`}>
                    {message.text}
                </div>
            )}

            <div className="form-card rating-card">
                <p>Рейтинг: {renderRating()}</p>
                <div className="rating-stars">
                    <span>Ваша оценка:</span>
                    {[1, 2, 3, 4, 5].map(score => (
                        <button
                            key={score}
                            type="button"
                            className="star-btn"
                            title={`Оценить на ${score}`}
                            onClick={() => handleRate(score)}
                        >
                            {score} ★
                        </button>
                    ))}
                </div>
            </div>

            <div className="form-card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group comments-section">
                        <label htmlFor="commentText">Ваш комментарий:</label>
                        <textarea
                            id="commentText"
                            value={commentText}
                            onChange={(e) => setCommentText(e.target.value)}
                            placeholder="Напишите свой отзыв..."
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">
                        Отправить комментарий
                    </button>
                </form>
            </div>

            <div className="comments-section">
                <h2>Комментарии ({comments.length})</h2>
                {comments.length === 0 ? (
                    <p className="no-comments">Пока нет комментариев.</p>
                ) : (
                    <ul className="comment-items">
                        {comments.map((comment, index) => (
                            <li key={index} className="comment-item">
                                <div className="comment-header">
                                    <strong>{comment.user?.username || 'Аноним'}</strong> —{' '}
                                    <small>
                                        {comment.createAt
                                            ? new Date(comment.createAt).toLocaleString('ru-RU')
                                            : '—'}
                                    </small>
                                </div>
                                <p className="comment-text">{comment.text}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            <button className="back-link" onClick={() => navigate('/list')}>
                ← Назад к списку
            </button>
        </div>
    );
}
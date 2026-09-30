import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';

export default function BookView() {
    const { id } = useParams(); // из URL: /book/5/comment
    const [commentText, setCommentText] = useState('');
    const [comments, setComments] = useState([]);
    const [message, setMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();

    // Загружаем комментарии при старте
    useEffect(() => {
        loadComments();
    }, [id]);

    const loadComments = async () => {
        try {
            const res = await api.get(`/book/api/v1/${id}`);
            setComments(res.data.value.comments || []);
        } catch (err) {
            setMessage({ type: 'error', text: 'Не удалось загрузить комментарии' });
        }
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
            <h1>💬 Добавить комментарий</h1>
            <p className="book-id">Книга ID: <strong>{id}</strong></p>

            {message.text && (
                <div className={`message-box ${message.type}`}>
                    {message.text}
                </div>
            )}

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

            <button className="back-link" onClick={() => navigate('/')}>
                ← Назад к списку
            </button>
        </div>
    );
}
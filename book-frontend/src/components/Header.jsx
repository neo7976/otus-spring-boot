
import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Header() {
    const navigate = useNavigate();
    const isAuthenticated = !!localStorage.getItem('authToken');

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        // Также можно очистить другие данные (например, пользователя)
        navigate('/login');
    };

    return (
        <header className="app-header">
            <h2>📚 Библиотека</h2>
            {isAuthenticated && (
                <button onClick={handleLogout} className="btn btn-outline-danger">
                    Выйти
                </button>
            )}
        </header>
    );
}
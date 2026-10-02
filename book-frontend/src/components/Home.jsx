// src/components/Home.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/home.css';

export default function Home() {
    return (
        <div className="home-container">
            <h1>📚 Добро пожаловать в Библиотеку!</h1>
            <p>Выберите действие:</p>
            <div className="home-actions">
                <Link to="/list" className="btn btn-primary">📖 Список книг</Link>
                <Link to="/book/create" className="btn btn-secondary">➕ Добавить книгу</Link>
                <Link to="/login" className="btn btn-outline-primary">🔐 Войти</Link>
            </div>
        </div>
    );
}
// src/App.js
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header'; // ← новый компонент
import Login from './components/Login';
import Home from './components/Home';
import BookList from './components/BookList';
import CreateBook from './components/CreateBook';
import BookView from './components/BookView';


function App() {
    return (
        <BrowserRouter>
            <div className="App">
                <Header />
                <main className="app-main">
                    <Routes>
                        <Route path="/login" element={<Login />} />
                        <Route path="/" element={<Home />} />
                        <Route path="/list" element={<BookList />} />
                        <Route path="/book/create" element={<CreateBook />} />
                        <Route path="/book/:id/comment" element={<BookView />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;
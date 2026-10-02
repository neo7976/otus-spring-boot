// src/App.js
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header'; // ← новый компонент
import Login from './components/Login';
import Home from './components/Home';
import BookList from './components/BookList';
import CreateBook from './components/CreateBook';
import BookView from './components/BookView';
import PrivateRoute from './components/PrivateRoute';


function App() {
    return (
        <BrowserRouter>
            <div className="App">
                <Header />
                <main className="app-main">
                    <Routes>
                        <Route path="/login" element={<Login />} />
                        <Route path="/" element={<PrivateRoute><Home /></PrivateRoute>} />
                        <Route path="/list" element={<PrivateRoute><BookList /></PrivateRoute>} />
                        <Route path="/book/create" element={<PrivateRoute><CreateBook /></PrivateRoute>} />
                        <Route path="/book/:id" element={<PrivateRoute><BookView /></PrivateRoute>} />
                        <Route path="/book/:id/comment" element={<PrivateRoute><BookView /></PrivateRoute>} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;
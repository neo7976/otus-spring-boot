import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:9000', // ваш Spring Boot
    timeout: 10000,
});

// Добавляем токен к каждому запросу
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Обработка 401 (автоматический выход)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
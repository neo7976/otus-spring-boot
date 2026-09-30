// Работа с JWT на Thymeleaf-страницах: токен хранится в localStorage
// и передаётся в заголовке Authorization при каждом запросе к REST API.
const AUTH_TOKEN_KEY = 'jwtToken';

function getToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function saveToken(token) {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
}

function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    window.location.href = '/login';
}

// Полезная нагрузка токена (без проверки подписи — только для отображения в UI)
function getTokenPayload() {
    const token = getToken();
    if (!token) return null;
    try {
        const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(decodeURIComponent(escape(atob(payload))));
    } catch (e) {
        return null;
    }
}

function requireAuth() {
    const payload = getTokenPayload();
    if (!payload || payload.exp * 1000 < Date.now()) {
        logout();
    }
}

function hasRole(role) {
    const payload = getTokenPayload();
    return !!payload && (payload.roles || []).includes(role);
}

async function authFetch(url, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = getToken();
    if (token) {
        headers.set('Authorization', 'Bearer ' + token);
    }
    const response = await fetch(url, {...options, headers});
    if (response.status === 401) {
        logout();
    }
    return response;
}

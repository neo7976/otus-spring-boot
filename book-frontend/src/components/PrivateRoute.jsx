
import { Navigate } from 'react-router-dom';

export default function PrivateRoute({ children }) {
    const isAuthenticated = !!localStorage.getItem('authToken');
    return isAuthenticated ? children : <Navigate to="/login" />;
}
import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import AdminConsultaGrid from './pages/consulta/AdminConsultaGrid';

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/home" replace />,
                },
                {
                    path: 'home',
                    element: <Home />,
                },
                {
                    path: 'admin/consulta',
                    element: <AdminConsultaGrid />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);

import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import ConsultaGrid from './pages/consulta/ConsultaGrid';

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
                    path: 'consulta',
                    element: <ConsultaGrid />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);

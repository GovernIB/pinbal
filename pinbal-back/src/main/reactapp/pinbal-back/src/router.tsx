import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import ConsultaGrid from './pages/consulta/ConsultaGrid';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import ServeiGrid from './pages/servei/ServeiGrid';
import ServeiForm from './pages/servei/ServeiForm';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid';
import ProcedimentForm from './pages/procediment/ProcedimentForm';
import OrganGestorGrid from './pages/organgestor/OrganGestorGrid';
import ParamConfGrid from './pages/paramconf/ParamConfGrid';
import ParamConfForm from './pages/paramconf/ParamConfForm';
import EmissorCertGrid from './pages/emissorcert/EmissorCertGrid';
import EmissorCertForm from './pages/emissorcert/EmissorCertForm';
import ClauPublicaGrid from './pages/claupublica/ClauPublicaGrid';
import ClauPublicaForm from './pages/claupublica/ClauPublicaForm';
import ClauPrivadaGrid from './pages/clauprivada/ClauPrivadaGrid';
import ClauPrivadaForm from './pages/clauprivada/ClauPrivadaForm';
import AvisGrid from './pages/avis/AvisGrid';
import AvisForm from './pages/avis/AvisForm';
import CacheGrid from './pages/cache/CacheGrid';
import Propietats from './pages/propietat/Propietats';

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
                {
                    path: 'entitats',
                    element: <EntitatGrid />,
                },
                {
                    path: 'entitats/form',
                    element: <EntitatForm />,
                },
                {
                    path: 'entitats/form/:id',
                    element: <EntitatForm />,
                },
                {
                    path: 'serveis',
                    element: <ServeiGrid />,
                },
                {
                    path: 'serveis/form/:id',
                    element: <ServeiForm />,
                },
                {
                    path: 'procediments',
                    element: <ProcedimentGrid />,
                },
                {
                    path: 'procediments/form',
                    element: <ProcedimentForm />,
                },
                {
                    path: 'procediments/form/:id',
                    element: <ProcedimentForm />,
                },
                {
                    path: 'organgestors',
                    element: <OrganGestorGrid />,
                },
                {
                    path: 'paramconfs',
                    element: <ParamConfGrid />,
                },
                {
                    path: 'paramconfs/form',
                    element: <ParamConfForm />,
                },
                {
                    path: 'paramconfs/form/:id',
                    element: <ParamConfForm />,
                },
                {
                    path: 'emissorcerts',
                    element: <EmissorCertGrid />,
                },
                {
                    path: 'emissorcerts/form',
                    element: <EmissorCertForm />,
                },
                {
                    path: 'emissorcerts/form/:id',
                    element: <EmissorCertForm />,
                },
                {
                    path: 'claupubliques',
                    element: <ClauPublicaGrid />,
                },
                {
                    path: 'claupubliques/form',
                    element: <ClauPublicaForm />,
                },
                {
                    path: 'claupubliques/form/:id',
                    element: <ClauPublicaForm />,
                },
                {
                    path: 'clauprivades',
                    element: <ClauPrivadaGrid />,
                },
                {
                    path: 'clauprivades/form',
                    element: <ClauPrivadaForm />,
                },
                {
                    path: 'clauprivades/form/:id',
                    element: <ClauPrivadaForm />,
                },
                {
                    path: 'avisos',
                    element: <AvisGrid />,
                },
                {
                    path: 'avisos/form',
                    element: <AvisForm />,
                },
                {
                    path: 'avisos/form/:id',
                    element: <AvisForm />,
                },
                {
                    path: 'caches',
                    element: <CacheGrid />,
                },
                {
                    path: 'propietats',
                    element: <Propietats />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);

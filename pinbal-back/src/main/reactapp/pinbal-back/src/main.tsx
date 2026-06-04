import React from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { LicenseInfo } from '@mui/x-license';
import { router } from './router';

LicenseInfo.setLicenseKey(
    'e0bde345c6cb2453171a44e15a0c58f5Tz0xMjQ4NTIsRT0xODAxMDk0Mzk5MDAwLFM9cHJvLExNPXN1YnNjcmlwdGlvbixQVj1pbml0aWFsLEtWPTI='
);

createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>
);

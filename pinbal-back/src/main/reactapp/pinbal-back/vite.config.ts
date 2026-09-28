import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vite.dev/config/
export default defineConfig(({ command, mode, isPreview }) => {
    // Load env file based on `mode` in the current working directory.
    // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
    const env = loadEnv(mode, process.cwd(), '');
    // En desenvolupament i en el build per JBoss l'aplicació es serveix des de
    // <context path del backoffice>/reactapp (VITE_CONTEXT_PATH ve del build Maven o del .env).
    const contextPath = env.VITE_CONTEXT_PATH || '/pinbalback';
    const base = (command === 'serve' && !isPreview) || mode === 'jboss' ? `${contextPath}/reactapp` : undefined;

    return {
        base,
        preview: {
            port: 5173,
        },
        server: {
            open: env.DISABLE_OPEN_ON_START !== 'true',
            hmr: {
                clientPort: 5173, // TODO Documentar esto
            },
        },
        plugins: [react(), tsconfigPaths()],
    };
});

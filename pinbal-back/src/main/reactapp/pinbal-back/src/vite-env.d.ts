/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_API_URL: string
    readonly VITE_API_PUBLIC_URL: string
    readonly VITE_API_BASE_URL: string
    readonly VITE_API_SUFFIX: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Backend Spring Boot dziala domyslnie na :8080.
    // Proxy pozwala wolac /api/... z frontendu bez konfiguracji CORS po stronie backendu.
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // Statyczne obrazki produktow serwowane przez Spring z static/images.
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})

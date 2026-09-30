import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Dev server runs on http://localhost:5173
// Any call to /api/... is forwarded to the Spring Boot backend on port 8080
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        flow: {
          500: '#8b5cf6',
          600: '#7c3aed',
        }
      }
    },
  },
  plugins: [],
}

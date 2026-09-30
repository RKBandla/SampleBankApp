import { useState } from 'react'
import { login, register, saveToken } from '../api.js'

export default function Login({ onLogin }) {
  const [mode, setMode] = useState('login') // 'login' or 'register'
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      if (mode === 'register') {
        await register(username, password)
        setMessage('Registered! Please log in.')
        setMode('login')
      } else {
        const data = await login(username, password)
        saveToken(data.token)
        onLogin()
      }
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="card">
      <h2>{mode === 'login' ? 'Login' : 'Register'}</h2>

      <form onSubmit={handleSubmit}>
        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">{mode === 'login' ? 'Login' : 'Register'}</button>
      </form>

      {error && <p className="error">{error}</p>}
      {message && <p className="success">{message}</p>}

      <p className="switch">
        {mode === 'login' ? "Don't have an account? " : 'Already registered? '}
        <button
          className="link"
          onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); setMessage('') }}
        >
          {mode === 'login' ? 'Register' : 'Login'}
        </button>
      </p>
    </div>
  )
}

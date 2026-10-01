import { useState } from 'react'
import { login, register } from '../services/DataService.js'

const EMPTY_FORM = { username: '', password: '', firstName: '', lastName: '', email: '' }

// Login + Register in one component. "mode" comes from the parent (App).
// onLogin / onModeChange / notify are callbacks back to the parent.
export default function LoginPage({ mode, onModeChange, onLogin, notify }) {
  const [form, setForm] = useState(EMPTY_FORM)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [shake, setShake] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const isRegister = mode === 'register'

  // one handler for every input: uses the input's "name" to update that field
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  // wrong password → shake the card + pop-up message
  const fail = (message) => {
    setError(message)
    notify('error', message)
    setShake(true)
    setTimeout(() => setShake(false), 500)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (isRegister && form.username.trim().toLowerCase() === 'admin') {
      fail("The username 'admin' is reserved")
      return
    }

    setLoading(true)
    try {
      if (isRegister) {
        await register(form)
        notify('success', 'Account created! Please log in.')
        setForm({ ...EMPTY_FORM, username: form.username })
        onModeChange('login')
      } else {
        const data = await login(form.username, form.password)
        onLogin({ token: data.token, role: data.role, customerId: data.customerId, username: data.username })
      }
    } catch (err) {
      fail(err.message)
    } finally {
      setLoading(false)
    }
  }

  const switchMode = () => {
    setError('')
    onModeChange(isRegister ? 'login' : 'register')
  }

  return (
    <div className="auth-wrap">
      <div className={`card auth-card slide-up ${shake ? 'shake' : ''} ${error ? 'has-error' : ''}`}>
        <h2>{isRegister ? 'Open an account' : 'Welcome back'}</h2>
        <p className="muted">
          {isRegister ? 'You will get a Checking and a Savings account.' : 'Log in as a customer or admin.'}
        </p>

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <div className="row-2">
              <input name="firstName" placeholder="First name" value={form.firstName} onChange={handleChange} required />
              <input name="lastName" placeholder="Last name" value={form.lastName} onChange={handleChange} />
            </div>
          )}
          {isRegister && (
            <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} />
          )}

          <input name="username" placeholder="Username" value={form.username} onChange={handleChange}
                 autoComplete="username" required />

          <div className="password-field">
            <input name="password" type={showPassword ? 'text' : 'password'} placeholder="Password"
                   value={form.password} onChange={handleChange}
                   autoComplete={isRegister ? 'new-password' : 'current-password'} required />
            <button type="button" className="link small" onClick={() => setShowPassword(!showPassword)}>
              {showPassword ? 'Hide' : 'Show'}
            </button>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? <span className="btn-spinner" /> : isRegister ? 'Create account' : 'Login'}
          </button>
        </form>

        {error && <p className="error fade-in">{error}</p>}

        <p className="switch">
          {isRegister ? 'Already have an account? ' : "Don't have an account? "}
          <button className="link" onClick={switchMode}>{isRegister ? 'Login' : 'Register'}</button>
        </p>
      </div>
    </div>
  )
}

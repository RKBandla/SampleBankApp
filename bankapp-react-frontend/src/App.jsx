import { useState } from 'react'
import Login from './components/Login.jsx'
import Customers from './components/Customers.jsx'
import { getToken, clearToken } from './api.js'

export default function App() {
  const [loggedIn, setLoggedIn] = useState(!!getToken())

  const logout = () => {
    clearToken()
    setLoggedIn(false)
  }

  return (
    <div className="container">
      <header>
        <h1>Sample Bank App</h1>
        {loggedIn && <button className="secondary" onClick={logout}>Logout</button>}
      </header>

      {loggedIn
        ? <Customers onSessionExpired={logout} />
        : <Login onLogin={() => setLoggedIn(true)} />}
    </div>
  )
}

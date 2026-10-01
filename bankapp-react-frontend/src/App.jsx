import { useState } from 'react'
import Header from './components/Header.jsx'
import Footer from './components/Footer.jsx'
import WelcomePage from './components/WelcomePage.jsx'
import LoginPage from './components/LoginPage.jsx'
import AdminDashboard from './components/admin/AdminDashboard.jsx'
import CustomerDashboard from './components/customer/CustomerDashboard.jsx'
import ToastContainer from './components/common/ToastContainer.jsx'
import { getSession, saveSession, clearSession } from './services/DataService.js'

// App is the top PARENT component.
// It owns the shared state and passes it DOWN as props.
// Children talk back UP by calling the callback functions it passes them.
export default function App() {
  const [session, setSession] = useState(getSession())   // { token, role, customerId, username }
  const [page, setPage] = useState('welcome')            // 'welcome' | 'login' | 'register'
  const [toasts, setToasts] = useState([])

  // child -> parent: any child can show a pop-up message
  const notify = (type, message) => {
    const id = Date.now() + Math.random()
    setToasts((list) => [...list, { id, type, message }])
    setTimeout(() => setToasts((list) => list.filter((t) => t.id !== id)), 3500)
  }

  // child -> parent: LoginPage calls this after a successful login
  const handleLogin = (newSession) => {
    saveSession(newSession)
    setSession(newSession)
    notify('success', `Welcome, ${newSession.username}!`)
  }

  const handleLogout = (message) => {
    clearSession()
    setSession(null)
    setPage('login')
    if (message) notify('error', message)
  }

  const renderPage = () => {
    if (session?.role === 'ADMIN') {
      return <AdminDashboard notify={notify} onSessionExpired={() => handleLogout('Session expired. Please log in again.')} />
    }
    if (session?.role === 'CUSTOMER') {
      return (
        <CustomerDashboard
          customerId={session.customerId}
          notify={notify}
          onSessionExpired={() => handleLogout('Session expired. Please log in again.')}
        />
      )
    }
    if (page === 'login' || page === 'register') {
      return <LoginPage mode={page} onModeChange={setPage} onLogin={handleLogin} notify={notify} />
    }
    return <WelcomePage onNavigate={setPage} />
  }

  return (
    <div className="app">
      <Header session={session} onNavigate={setPage} onLogout={() => handleLogout()} />
      <main className="container">{renderPage()}</main>
      <Footer />
      <ToastContainer toasts={toasts} />
    </div>
  )
}

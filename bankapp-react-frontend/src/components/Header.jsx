// CHILD of App. Receives data (session) DOWN as props,
// and sends events UP with onNavigate / onLogout.
export default function Header({ session, onNavigate, onLogout }) {
  return (
    <header className="header">
      <div className="header-inner">
        <button className="brand" onClick={() => !session && onNavigate('welcome')}>
          <span className="logo">SB</span> Sample Bank
        </button>

        <nav>
          {session ? (
            <>
              <span className={`badge ${session.role === 'ADMIN' ? 'badge-admin' : 'badge-customer'}`}>
                {session.role === 'ADMIN' ? 'Admin' : 'Customer'}
              </span>
              <span className="user">{session.username}</span>
              <button className="secondary" onClick={onLogout}>Logout</button>
            </>
          ) : (
            <>
              <button className="ghost" onClick={() => onNavigate('login')}>Login</button>
              <button onClick={() => onNavigate('register')}>Open an account</button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}

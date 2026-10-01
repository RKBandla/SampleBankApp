// Welcome page: the first thing a visitor sees.
// onNavigate is a callback from App (child -> parent).
export default function WelcomePage({ onNavigate }) {
  const features = [
    { title: 'Checking & Savings', text: 'Every customer gets two accounts, ready to use.' },
    { title: 'Instant transfers', text: 'Move money between your accounts or to another customer.' },
    { title: 'Secure by design', text: 'Passwords hashed with BCrypt, every request protected by JWT.' },
  ]

  return (
    <div className="welcome fade-in">
      <section className="hero">
        <h1>Banking made simple.</h1>
        <p>View balances, deposit, and transfer money — all in one place.</p>
        <div className="hero-actions">
          <button onClick={() => onNavigate('register')}>Get started</button>
          <button className="ghost" onClick={() => onNavigate('login')}>I already have an account</button>
        </div>
      </section>

      <section className="features">
        {features.map((f) => (
          <div className="card feature" key={f.title}>
            <h3>{f.title}</h3>
            <p>{f.text}</p>
          </div>
        ))}
      </section>
    </div>
  )
}

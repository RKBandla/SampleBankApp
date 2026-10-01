import { useEffect, useState } from 'react'
import { getCustomerById } from '../../services/DataService.js'
import { formatMoney, shortId } from '../../services/format.js'
import Spinner from '../common/Spinner.jsx'
import ErrorMessage from '../common/ErrorMessage.jsx'

// Shows ONE customer (GET /api/customers/{id}) and their accounts.
// Reloads whenever the customerId prop changes.
export default function CustomerDetails({ customerId, onClose, onSessionExpired }) {
  const [customer, setCustomer] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      setCustomer(await getCustomerById(customerId))
    } catch (err) {
      if (err.message === 'SESSION_EXPIRED') onSessionExpired()
      else setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [customerId])

  return (
    <div className="card details slide-up">
      <div className="page-title">
        <h2>Customer details</h2>
        <button className="secondary" onClick={onClose}>Close</button>
      </div>

      {loading ? <Spinner message="Loading customer..." /> : error ? <ErrorMessage message={error} onRetry={load} /> : (
        <>
          <p>
            <strong>{customer.firstName} {customer.lastName}</strong>
            {customer.premium && <span className="premium">★ Premium</span>}
            <br /><span className="muted">{customer.email || 'No email'} · ID {customer.id}</span>
          </p>
          <div className="accounts">
            {customer.accounts.map((a) => (
              <div key={a.id} className={`account account-${a.type.toLowerCase()}`}>
                <span className="account-type">{a.type === 'CHECKING' ? 'Checking' : 'Savings'}</span>
                <span className="account-balance">{formatMoney(a.balance)}</span>
                <span className="account-number">{shortId(a.id)}</span>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  )
}

import { useState } from 'react'
import { deposit } from '../../services/DataService.js'
import { formatMoney } from '../../services/format.js'

// CHILD of CustomerDashboard. Tells the parent when done (onDone) or failed (onError).
export default function DepositForm({ customerId, accounts, onDone, onError }) {
  const [accountId, setAccountId] = useState(accounts[0]?.id || '')
  const [amount, setAmount] = useState('')
  const [saving, setSaving] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await deposit(customerId, accountId, Number(amount))
      onDone(`Deposited ${formatMoney(amount)}`)
      setAmount('')
    } catch (err) {
      onError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h3>Deposit</h3>
      <label>To account
        <select value={accountId} onChange={(e) => setAccountId(e.target.value)}>
          {accounts.map((a) => (
            <option key={a.id} value={a.id}>{a.type === 'CHECKING' ? 'Checking' : 'Savings'} ({formatMoney(a.balance)})</option>
          ))}
        </select>
      </label>
      <label>Amount
        <input type="number" min="0.01" step="0.01" placeholder="0.00" value={amount}
               onChange={(e) => setAmount(e.target.value)} required />
      </label>
      <button type="submit" disabled={saving}>{saving ? <span className="btn-spinner" /> : 'Deposit'}</button>
    </form>
  )
}

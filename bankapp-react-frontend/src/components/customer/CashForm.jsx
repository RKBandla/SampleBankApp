import { useState } from 'react'
import { deposit, withdraw } from '../../services/DataService.js'
import { formatMoney } from '../../services/format.js'

// Deposit OR Withdraw (switch with the two tabs at the top).
// CHILD of CustomerDashboard: reports back with onDone / onError (child -> parent).
export default function CashForm({ customerId, accounts, onDone, onError }) {
  const [mode, setMode] = useState('deposit')        // 'deposit' | 'withdraw'
  const [accountId, setAccountId] = useState(accounts[0]?.id || '')
  const [amount, setAmount] = useState('')
  const [saving, setSaving] = useState(false)

  const isDeposit = mode === 'deposit'

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (isDeposit) {
        await deposit(customerId, accountId, Number(amount))
        onDone(`Deposited ${formatMoney(amount)}`)
      } else {
        await withdraw(customerId, accountId, Number(amount))
        onDone(`Withdrew ${formatMoney(amount)}`)
      }
      setAmount('')
    } catch (err) {
      onError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <div className="tabs" role="tablist">
        <button type="button" role="tab" aria-selected={isDeposit}
                className={`tab ${isDeposit ? 'active' : ''}`} onClick={() => setMode('deposit')}>Deposit</button>
        <button type="button" role="tab" aria-selected={!isDeposit}
                className={`tab ${!isDeposit ? 'active' : ''}`} onClick={() => setMode('withdraw')}>Withdraw</button>
      </div>

      <label>{isDeposit ? 'To account' : 'From account'}
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
      <button type="submit" className={isDeposit ? '' : 'danger'} disabled={saving}>
        {saving ? <span className="btn-spinner" /> : isDeposit ? 'Deposit' : 'Withdraw'}
      </button>
    </form>
  )
}

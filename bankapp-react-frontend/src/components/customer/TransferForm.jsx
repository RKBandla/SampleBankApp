import { useState } from 'react'
import { transfer } from '../../services/DataService.js'
import { formatMoney } from '../../services/format.js'

const OTHER = 'OTHER'

// CHILD of CustomerDashboard. Transfer between my own accounts, or to another customer's account number.
export default function TransferForm({ customerId, accounts, onDone, onError }) {
  const [fromId, setFromId] = useState(accounts[0]?.id || '')
  const [toChoice, setToChoice] = useState(accounts[1]?.id || OTHER)
  const [otherAccount, setOtherAccount] = useState('')
  const [amount, setAmount] = useState('')
  const [saving, setSaving] = useState(false)

  const label = (a) => `${a.type === 'CHECKING' ? 'Checking' : 'Savings'} (${formatMoney(a.balance)})`

  const handleSubmit = async (e) => {
    e.preventDefault()
    const toId = toChoice === OTHER ? otherAccount.trim() : toChoice
    if (toId === fromId) {
      onError('Choose two different accounts')
      return
    }
    setSaving(true)
    try {
      await transfer(customerId, fromId, toId, Number(amount))
      onDone(`Transferred ${formatMoney(amount)}`)
      setAmount('')
      setOtherAccount('')
    } catch (err) {
      onError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h3>Transfer</h3>
      <label>From
        <select value={fromId} onChange={(e) => setFromId(e.target.value)}>
          {accounts.map((a) => <option key={a.id} value={a.id}>{label(a)}</option>)}
        </select>
      </label>
      <label>To
        <select value={toChoice} onChange={(e) => setToChoice(e.target.value)}>
          {accounts.filter((a) => a.id !== fromId).map((a) => <option key={a.id} value={a.id}>My {label(a)}</option>)}
          <option value={OTHER}>Another customer's account…</option>
        </select>
      </label>
      {toChoice === OTHER && (
        <input placeholder="Paste their account number" value={otherAccount}
               onChange={(e) => setOtherAccount(e.target.value)} required />
      )}
      <label>Amount
        <input type="number" min="0.01" step="0.01" placeholder="0.00" value={amount}
               onChange={(e) => setAmount(e.target.value)} required />
      </label>
      <button type="submit" disabled={saving}>{saving ? <span className="btn-spinner" /> : 'Transfer'}</button>
    </form>
  )
}

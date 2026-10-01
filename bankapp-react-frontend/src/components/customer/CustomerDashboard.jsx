import { useEffect, useState } from 'react'
import { getCustomerDashboard, getAdminDashboard } from '../../services/DataService.js'
import { formatMoney } from '../../services/format.js'
import Spinner from '../common/Spinner.jsx'
import ErrorMessage from '../common/ErrorMessage.jsx'
import AccountCard from './AccountCard.jsx'
import CashForm from './CashForm.jsx'
import TransferForm from './TransferForm.jsx'
import TransactionList from './TransactionList.jsx'

// DISPLAY PAGE for customers: GET /api/customerDashboard/{id}
// PARENT of AccountCard, DepositForm, TransferForm, TransactionList.
export default function CustomerDashboard({ customerId, notify, onSessionExpired }) {
  const [data, setData] = useState(null)        // { customer: {..., accounts, totalBalance}, transactions }
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const handleError = (err) => {
    if (err.message === 'SESSION_EXPIRED') onSessionExpired()
    else setError(err.message)
  }

  const load = async (showSpinner = true) => {
    if (showSpinner) setLoading(true)
    setError('')
    try {
      setData(await getCustomerDashboard(customerId))
    } catch (err) {
      handleError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [customerId])

  // child -> parent: Deposit/Transfer finished → refresh balances without the big spinner
  const handleDone = (message) => {
    notify('success', message)
    load(false)
  }

  // Demo of authorization: a CustomerToken is NOT allowed on /api/admin → 403
  const tryAdmin = async () => {
    try {
      await getAdminDashboard()
      notify('success', 'Admin dashboard opened')
    } catch (err) {
      if (err.message === 'SESSION_EXPIRED') onSessionExpired()
      else notify('error', `${err.status} ${err.message}`)
    }
  }

  if (loading) return <Spinner message="Loading your accounts..." />
  if (error) return <ErrorMessage message={error} onRetry={() => load()} />

  const { customer, transactions } = data

  return (
    <div className="dashboard fade-in">
      <div className="page-title">
        <div>
          <h1>Hello, {customer.firstName}</h1>
          <p className="muted">
            Total balance <strong className="total">{formatMoney(customer.totalBalance)}</strong>
            {customer.premium && <span className="premium">★ Premium</span>}
          </p>
        </div>
        <button className="ghost" onClick={tryAdmin} title="Shows that customers get 403 on /api/admin">
          Try Admin Dashboard
        </button>
      </div>

      <div className="accounts">
        {customer.accounts.map((a) => <AccountCard key={a.id} account={a} notify={notify} />)}
      </div>

      <div className="grid-2">
        <CashForm customerId={customerId} accounts={customer.accounts}
                  onDone={handleDone} onError={(m) => notify('error', m)} />
        <TransferForm customerId={customerId} accounts={customer.accounts}
                      onDone={handleDone} onError={(m) => notify('error', m)} />
      </div>

      <TransactionList transactions={transactions} />
    </div>
  )
}

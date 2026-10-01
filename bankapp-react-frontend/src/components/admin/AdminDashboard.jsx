import { useEffect, useState } from 'react'
import {
  getAdminDashboard, getAllCustomers, getPremiumCustomers,
  findCustomersByFirstName, deleteCustomer,
} from '../../services/DataService.js'
import { formatMoney } from '../../services/format.js'
import StatCard from '../common/StatCard.jsx'
import Spinner from '../common/Spinner.jsx'
import ErrorMessage from '../common/ErrorMessage.jsx'
import EmptyState from '../common/EmptyState.jsx'
import SearchFilterBar from './SearchFilterBar.jsx'
import CustomerTable from './CustomerTable.jsx'
import CustomerForm from './CustomerForm.jsx'
import CustomerDetails from './CustomerDetails.jsx'

// DISPLAY PAGE for admins: calls the API and shows the results.
// It is the PARENT of SearchFilterBar, CustomerTable, CustomerForm and CustomerDetails.
export default function AdminDashboard({ notify, onSessionExpired }) {
  const [stats, setStats] = useState(null)
  const [customers, setCustomers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState({ mode: 'all', query: '' })   // all | premium | search
  const [selectedId, setSelectedId] = useState(null)
  const [showForm, setShowForm] = useState(false)

  const handleError = (err) => {
    if (err.message === 'SESSION_EXPIRED') onSessionExpired()
    else setError(err.message)
  }

  // Load the summary numbers + the customer list for the current filter
  const load = async (currentFilter = filter) => {
    setLoading(true)
    setError('')
    try {
      const [dashboard, list] = await Promise.all([
        getAdminDashboard(),
        currentFilter.mode === 'premium' ? getPremiumCustomers()
          : currentFilter.mode === 'search' ? findCustomersByFirstName(currentFilter.query)
          : getAllCustomers(),
      ])
      setStats(dashboard)
      setCustomers(list)
    } catch (err) {
      handleError(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  // child -> parent: SearchFilterBar tells us the new filter
  const handleFilterChange = (newFilter) => {
    setFilter(newFilter)
    load(newFilter)
  }

  // child -> parent: CustomerTable asks to delete a row
  const handleDelete = async (customer) => {
    if (!window.confirm(`Delete ${customer.firstName} and all their accounts?`)) return
    try {
      await deleteCustomer(customer.id)
      notify('success', `${customer.firstName} deleted`)
      if (selectedId === customer.id) setSelectedId(null)
      load()
    } catch (err) {
      handleError(err)
      notify('error', err.message)
    }
  }

  // child -> parent: CustomerForm created a customer
  const handleCreated = (customer) => {
    notify('success', `${customer.firstName} added with Checking + Savings accounts`)
    setShowForm(false)
    load()
  }

  return (
    <div className="dashboard fade-in">
      <div className="page-title">
        <h1>Admin Dashboard</h1>
        <button onClick={() => setShowForm(!showForm)}>{showForm ? 'Close' : '+ Add customer'}</button>
      </div>

      {stats && (
        <div className="stats">
          <StatCard label="Customers" value={stats.totalCustomers} />
          <StatCard label="Premium customers" value={stats.premiumCustomers} />
          <StatCard label="Accounts" value={stats.totalAccounts} />
          <StatCard label="Total deposits" value={formatMoney(stats.totalDeposits)} />
        </div>
      )}

      {showForm && <CustomerForm onCreated={handleCreated} onError={(m) => notify('error', m)} />}

      <div className="card">
        <h2>Customers</h2>
        <SearchFilterBar filter={filter} onFilterChange={handleFilterChange} />

        {loading ? (
          <Spinner message="Loading customers..." />
        ) : error ? (
          <ErrorMessage message={error} onRetry={() => load()} />
        ) : customers.length === 0 ? (
          <EmptyState
            message={filter.mode === 'search'
              ? `No customers found with first name "${filter.query}".`
              : filter.mode === 'premium' ? 'No premium customers yet.' : 'No customers yet.'}
          />
        ) : (
          <CustomerTable customers={customers} selectedId={selectedId}
                         onSelect={setSelectedId} onDelete={handleDelete} />
        )}
      </div>

      {selectedId && (
        <CustomerDetails customerId={selectedId} onClose={() => setSelectedId(null)} onSessionExpired={onSessionExpired} />
      )}
    </div>
  )
}

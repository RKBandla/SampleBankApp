import { useState } from 'react'
import { createCustomer } from '../../services/DataService.js'

// CHILD of AdminDashboard. POSTs a new customer, then tells the parent with onCreated.
export default function CustomerForm({ onCreated, onError }) {
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '' })
  const [saving, setSaving] = useState(false)

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const created = await createCustomer(form)
      setForm({ firstName: '', lastName: '', email: '' })
      onCreated(created)
    } catch (err) {
      onError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="card inline-form slide-up" onSubmit={handleSubmit}>
      <h3>New customer</h3>
      <div className="row-3">
        <input name="firstName" placeholder="First name" value={form.firstName} onChange={handleChange} required />
        <input name="lastName" placeholder="Last name" value={form.lastName} onChange={handleChange} />
        <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} />
      </div>
      <button type="submit" disabled={saving}>{saving ? <span className="btn-spinner" /> : 'Save customer'}</button>
    </form>
  )
}

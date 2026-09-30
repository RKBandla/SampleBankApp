import { useEffect, useState } from 'react'
import { getAllCustomers, createCustomer, updateCustomer, deleteCustomer } from '../api.js'

export default function Customers({ onSessionExpired }) {
  const [customers, setCustomers] = useState([])
  const [newName, setNewName] = useState('')
  const [editingId, setEditingId] = useState(null)
  const [editName, setEditName] = useState('')
  const [error, setError] = useState('')

  const handleError = (err) => {
    if (err.message === 'SESSION_EXPIRED') onSessionExpired()
    else setError(err.message)
  }

  const loadCustomers = async () => {
    try {
      setCustomers(await getAllCustomers())
    } catch (err) {
      handleError(err)
    }
  }

  useEffect(() => { loadCustomers() }, [])

  const handleAdd = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await createCustomer({ name: newName })
      setNewName('')
      loadCustomers()
    } catch (err) {
      handleError(err)
    }
  }

  const handleSave = async (id) => {
    setError('')
    try {
      await updateCustomer(id, { name: editName })
      setEditingId(null)
      loadCustomers()
    } catch (err) {
      handleError(err)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this customer?')) return
    setError('')
    try {
      await deleteCustomer(id)
      loadCustomers()
    } catch (err) {
      handleError(err)
    }
  }

  return (
    <div className="card">
      <h2>Customers</h2>

      <form className="row" onSubmit={handleAdd}>
        <input
          placeholder="New customer name"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          required
        />
        <button type="submit">Add</button>
      </form>

      {error && <p className="error">{error}</p>}

      {customers.length === 0 ? (
        <p className="empty">No customers yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {customers.map((c) => (
              <tr key={c.id}>
                <td className="id">{c.id}</td>
                <td>
                  {editingId === c.id ? (
                    <input value={editName} onChange={(e) => setEditName(e.target.value)} />
                  ) : (
                    c.name
                  )}
                </td>
                <td className="actions">
                  {editingId === c.id ? (
                    <>
                      <button onClick={() => handleSave(c.id)}>Save</button>
                      <button className="secondary" onClick={() => setEditingId(null)}>Cancel</button>
                    </>
                  ) : (
                    <>
                      <button className="secondary" onClick={() => { setEditingId(c.id); setEditName(c.name) }}>Edit</button>
                      <button className="danger" onClick={() => handleDelete(c.id)}>Delete</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

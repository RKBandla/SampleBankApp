import { formatMoney } from '../../services/format.js'

// CHILD of AdminDashboard. Displays the list it receives as a prop.
// Clicking View / Delete calls the parent's callbacks (child -> parent).
export default function CustomerTable({ customers, selectedId, onSelect, onDelete }) {
  const balanceOf = (c, type) => c.accounts?.find((a) => a.type === type)?.balance ?? 0

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th className="hide-sm">Email</th>
            <th className="num">Checking</th>
            <th className="num">Savings</th>
            <th className="num">Total</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {customers.map((c) => (
            <tr key={c.id} className={selectedId === c.id ? 'selected' : ''}>
              <td>
                {c.firstName} {c.lastName}
                {c.premium && <span className="premium">★ Premium</span>}
              </td>
              <td className="hide-sm muted">{c.email}</td>
              <td className="num">{formatMoney(balanceOf(c, 'CHECKING'))}</td>
              <td className="num">{formatMoney(balanceOf(c, 'SAVINGS'))}</td>
              <td className="num strong">{formatMoney(c.totalBalance)}</td>
              <td className="actions">
                <button className="secondary" onClick={() => onSelect(c.id)}>View</button>
                <button className="danger" onClick={() => onDelete(c)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

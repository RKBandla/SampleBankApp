import EmptyState from '../common/EmptyState.jsx'
import { formatMoney, formatDate, shortId } from '../../services/format.js'

const LABELS = { DEPOSIT: 'Deposit', WITHDRAW: 'Withdrawal', TRANSFER_IN: 'Transfer in', TRANSFER_OUT: 'Transfer out' }

export default function TransactionList({ transactions }) {
  return (
    <div className="card">
      <h3>Recent transactions</h3>
      {transactions.length === 0 ? (
        <EmptyState message="No transactions yet. Make a deposit to get started." />
      ) : (
        <ul className="tx-list">
          {transactions.map((t) => {
            const isOut = t.type === 'TRANSFER_OUT' || t.type === 'WITHDRAW'
            return (
              <li key={t.id} className="fade-in">
                <div>
                  <strong>{LABELS[t.type]}</strong>
                  <span className="muted small">
                    {formatDate(t.timestamp)} · acct {shortId(t.accountId)}
                    {t.otherAccountId && ` ${isOut ? '→' : '←'} ${shortId(t.otherAccountId)}`}
                  </span>
                </div>
                <span className={`amount ${isOut ? 'out' : 'in'}`}>
                  {isOut ? '−' : '+'}{formatMoney(t.amount)}
                </span>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}

import { formatMoney } from '../../services/format.js'

// One account (Checking or Savings). Data comes DOWN from CustomerDashboard as props.
export default function AccountCard({ account, notify }) {
  const copyNumber = async () => {
    try {
      await navigator.clipboard.writeText(account.id)
      notify('success', 'Account number copied')
    } catch {
      notify('error', 'Could not copy — select it manually')
    }
  }

  return (
    <div className={`account account-${account.type.toLowerCase()} pop-in`}>
      <span className="account-type">{account.type === 'CHECKING' ? 'Checking' : 'Savings'}</span>
      <span className="account-balance" key={account.balance}>{formatMoney(account.balance)}</span>
      <span className="account-number">
        Acct # {account.id}
        <button className="link small light" onClick={copyNumber}>Copy</button>
      </span>
    </div>
  )
}

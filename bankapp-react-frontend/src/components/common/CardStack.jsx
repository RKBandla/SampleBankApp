import CreditCard from './CreditCard.jsx'

// Three overlapping cards that float gently. Used on the Welcome and Login pages.
export default function CardStack({ compact = false }) {
  return (
    <div className={`card-stack ${compact ? 'compact' : ''}`} aria-hidden="true">
      <CreditCard variant="gold" label="Premium" last4="7730" holder="PRIYA SHARMA" expires="03/29" className="stack-back" />
      <CreditCard variant="green" label="Savings" last4="5512" holder="ROBERT BROWN" expires="11/28" className="stack-middle" />
      <CreditCard variant="blue" label="Checking" last4="4821" holder="ALEX MORGAN" expires="09/30" className="stack-front" />
    </div>
  )
}

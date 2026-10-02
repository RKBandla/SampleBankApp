// A decorative bank card drawn with CSS + SVG (no image files needed).
// Props come DOWN from the parent: which colour, label, last 4 digits, holder name.
export default function CreditCard({ variant = 'blue', label = 'Debit', last4 = '4821', holder = 'ALEX MORGAN', expires = '09/30', className = '' }) {
  return (
    <div className={`cc cc-${variant} ${className}`} aria-hidden="true">
      <div className="cc-top">
        <span className="cc-bank"><span className="cc-logo">SB</span> Sample Bank</span>
        <span className="cc-label">{label}</span>
      </div>

      <div className="cc-middle">
        {/* EMV chip */}
        <svg className="cc-chip" viewBox="0 0 48 36" width="46" height="34">
          <rect x="1" y="1" width="46" height="34" rx="6" fill="#e8c873" stroke="#b8963e" />
          <path d="M1 12h14M1 24h14M33 12h14M33 24h14M15 1v34M33 1v34M15 18h18" stroke="#b8963e" strokeWidth="1.5" fill="none" />
        </svg>
        {/* contactless waves */}
        <svg className="cc-contactless" viewBox="0 0 24 24" width="26" height="26">
          <path d="M8 7a7 7 0 0 1 0 10M12 4.5a11 11 0 0 1 0 15M16 2a15 15 0 0 1 0 20" stroke="currentColor"
                strokeWidth="2" fill="none" strokeLinecap="round" />
        </svg>
      </div>

      <div className="cc-number">•••• •••• •••• {last4}</div>

      <div className="cc-bottom">
        <div>
          <small>Card holder</small>
          <span>{holder}</span>
        </div>
        <div>
          <small>Expires</small>
          <span>{expires}</span>
        </div>
        <span className="cc-network">DEBIT</span>
      </div>
    </div>
  )
}

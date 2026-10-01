// The pop-up messages in the top-right corner
export default function ToastContainer({ toasts }) {
  return (
    <div className="toasts">
      {toasts.map((t) => (
        <div key={t.id} className={`toast toast-${t.type}`} role="status">
          <span className="toast-icon">{t.type === 'error' ? '!' : '✓'}</span>
          {t.message}
        </div>
      ))}
    </div>
  )
}

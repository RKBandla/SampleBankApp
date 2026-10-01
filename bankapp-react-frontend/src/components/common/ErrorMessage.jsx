// Shown when an API call fails. onRetry is a callback to the parent (child -> parent).
export default function ErrorMessage({ message, onRetry }) {
  return (
    <div className="error-box" role="alert">
      <strong>Something went wrong.</strong>
      <span>{message}</span>
      {onRetry && <button className="secondary" onClick={onRetry}>Try again</button>}
    </div>
  )
}

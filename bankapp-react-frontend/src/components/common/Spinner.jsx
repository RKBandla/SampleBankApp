// Shown while data is loading
export default function Spinner({ message = 'Loading...' }) {
  return (
    <div className="spinner-wrap" role="status">
      <div className="spinner" />
      <p>{message}</p>
    </div>
  )
}

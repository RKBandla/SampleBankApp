export default function StatCard({ label, value }) {
  return (
    <div className="card stat fade-in">
      <span className="stat-label">{label}</span>
      <span className="stat-value">{value}</span>
    </div>
  )
}

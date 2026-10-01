import { useState } from 'react'

// CHILD of AdminDashboard.
// Gets the current filter DOWN as a prop, sends the new filter UP with onFilterChange.
export default function SearchFilterBar({ filter, onFilterChange }) {
  const [query, setQuery] = useState(filter.query)

  const handleSearch = (e) => {
    e.preventDefault()
    onFilterChange(query.trim() ? { mode: 'search', query: query.trim() } : { mode: 'all', query: '' })
  }

  const showAll = () => {
    setQuery('')
    onFilterChange({ mode: 'all', query: '' })
  }

  return (
    <div className="toolbar">
      <form className="search" onSubmit={handleSearch}>
        <input placeholder="Search by first name..." value={query} onChange={(e) => setQuery(e.target.value)} />
        <button type="submit">Search</button>
      </form>

      <div className="chips">
        <button className={`chip ${filter.mode === 'all' ? 'active' : ''}`} onClick={showAll}>All</button>
        <button className={`chip ${filter.mode === 'premium' ? 'active' : ''}`}
                onClick={() => { setQuery(''); onFilterChange({ mode: 'premium', query: '' }) }}>
          ★ Premium
        </button>
      </div>
    </div>
  )
}

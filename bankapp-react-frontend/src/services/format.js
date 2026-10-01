// Small display helpers shared by components
const money = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' })

export const formatMoney = (value) => money.format(Number(value || 0))

export const shortId = (id) => (id ? `••••${String(id).slice(-6)}` : '')

export const formatDate = (iso) => (iso ? new Date(iso).toLocaleString() : '')

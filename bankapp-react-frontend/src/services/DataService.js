// DataService: the ONLY file that talks to the Spring Boot backend (using fetch).
// Components call these functions; they never build URLs or headers themselves.

const SESSION_KEY = 'session'

// ---------- session (token + role) saved in the browser ----------
export function getSession() {
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY))
  } catch {
    return null
  }
}
export const saveSession = (session) => localStorage.setItem(SESSION_KEY, JSON.stringify(session))
export const clearSession = () => localStorage.removeItem(SESSION_KEY)

// ---------- one shared request function ----------
async function request(method, url, body) {
  const headers = { 'Content-Type': 'application/json' }
  const session = getSession()
  if (session?.token) headers['Authorization'] = `Bearer ${session.token}`

  let response
  try {
    response = await fetch(url, { method, headers, body: body ? JSON.stringify(body) : undefined })
  } catch {
    throw apiError(0, 'Cannot reach the server. Is the backend running?')
  }

  const text = await response.text()
  let data = null
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    data = null
  }

  if (response.status === 401 && session?.token) {
    clearSession()                                   // token expired or invalid
    throw apiError(401, 'SESSION_EXPIRED')
  }
  if (!response.ok) {
    throw apiError(response.status, data?.error || `Request failed (${response.status})`)
  }
  return data
}

function apiError(status, message) {
  const err = new Error(message)
  err.status = status
  return err
}

// ---------- Auth ----------
export const register = (form) => request('POST', '/api/auth/register', form)
export const login = (username, password) => request('POST', '/api/auth/login', { username, password })

// ---------- Admin ----------
export const getAdminDashboard = () => request('GET', '/api/admin')
export const getAllCustomers = () => request('GET', '/api/customers')
export const getCustomerById = (id) => request('GET', `/api/customers/${id}`)
export const findCustomersByFirstName = (firstName) =>
  request('GET', `/api/customers/search?firstName=${encodeURIComponent(firstName)}`)
export const getPremiumCustomers = () => request('GET', '/api/customers/premium')
export const createCustomer = (customer) => request('POST', '/api/customers', customer)
export const deleteCustomer = (id) => request('DELETE', `/api/customers/${id}`)

// ---------- Customer ----------
export const getCustomerDashboard = (id) => request('GET', `/api/customerDashboard/${id}`)
export const deposit = (id, accountId, amount) =>
  request('POST', `/api/customerDashboard/${id}/deposit`, { accountId, amount })
export const withdraw = (id, accountId, amount) =>
  request('POST', `/api/customerDashboard/${id}/withdraw`, { accountId, amount })
export const transfer = (id, fromAccountId, toAccountId, amount) =>
  request('POST', `/api/customerDashboard/${id}/transfer`, { fromAccountId, toAccountId, amount })

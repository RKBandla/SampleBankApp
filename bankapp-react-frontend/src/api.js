// All calls to the Spring Boot backend live here.
// The token from login is saved in localStorage and sent as "Authorization: Bearer <token>".

const TOKEN_KEY = 'token'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const saveToken = (token) => localStorage.setItem(TOKEN_KEY, token)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

async function request(method, url, body) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`

  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401 && token) {
    clearToken() // token expired or invalid
    throw new Error('SESSION_EXPIRED')
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    throw new Error(data?.error || `Request failed (${response.status})`)
  }
  return data
}

// ---- Auth ----
export const register = (username, password) =>
  request('POST', '/api/auth/register', { username, password })

export const login = (username, password) =>
  request('POST', '/api/auth/login', { username, password })

// ---- Customers ----
export const getAllCustomers = () => request('GET', '/api/customers')
export const getCustomerById = (id) => request('GET', `/api/customers/${id}`)
export const createCustomer = (customer) => request('POST', '/api/customers', customer)
export const updateCustomer = (id, customer) => request('PUT', `/api/customers/${id}`, customer)
export const deleteCustomer = (id) => request('DELETE', `/api/customers/${id}`)

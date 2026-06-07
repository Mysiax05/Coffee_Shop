import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { getMe, login as apiLogin, logout as apiLogout } from '../api/endpoints'
import type { CustomerDto, LoginRequest } from '../api/types'

interface AuthContextValue {
  session: CustomerDto | null
  isLoggedIn: boolean
  loading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<CustomerDto | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMe()
      .then(setSession)
      .catch(() => setSession(null))
      .finally(() => setLoading(false))
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      isLoggedIn: session !== null,
      loading,
      login: async (credentials: LoginRequest) => {
        const customer = await apiLogin(credentials)
        setSession(customer)
      },
      logout: async () => {
        try {
          await apiLogout()
        } finally {
          setSession(null)
        }
      },
    }),
    [session, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth musi byc uzyte wewnatrz AuthProvider')
  return ctx
}

import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'

// UWAGA: backend nie ma jeszcze endpointu logowania.
// Tymczasowo "sesja" = zapamietany customerId (np. po rejestracji znasz swoje ID z bazy).
// Gdy w backendzie powstanie POST /api/.../login zwracajacy klienta,
// wystarczy podmienic implementacje login() ponizej (TODO) - reszta appki sie nie zmieni.

interface Session {
  customerId: number
}

interface AuthContextValue {
  session: Session | null
  isLoggedIn: boolean
  /** Tymczasowe: ustawia sesje na podstawie customerId. Docelowo: realny login. */
  loginWithId: (customerId: number) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)
const STORAGE_KEY = 'shop.session'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      return raw ? (JSON.parse(raw) as Session) : null
    } catch {
      return null
    }
  })

  useEffect(() => {
    if (session) localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    else localStorage.removeItem(STORAGE_KEY)
  }, [session])

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      isLoggedIn: session !== null,
      loginWithId: (customerId: number) => setSession({ customerId }),
      logout: () => setSession(null),
    }),
    [session],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth musi byc uzyte wewnatrz AuthProvider')
  return ctx
}

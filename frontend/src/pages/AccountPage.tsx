import { useEffect, useState } from 'react'
import { addAddress, changeEmail, deactivateAddress, getAddresses, registerCustomer } from '../api/endpoints'
import type { AddressDto } from '../api/types'
import { useAuth } from '../context/AuthContext'

const emptyReg = { firstName: '', lastName: '', email: '', phone: '', passwordHash: '' }
const emptyAddr = { label: '', street: '', city: '', postalCode: '', country: 'Poland' }
const emptyLogin = { email: '', password: '' }

export default function AccountPage() {
  const { session, isLoggedIn, login, logout, refresh } = useAuth()

  // --- Logowanie (e-mail + haslo) ---
  const [loginForm, setLoginForm] = useState(emptyLogin)
  const [loginMsg, setLoginMsg] = useState<string | null>(null)
  const [loggingIn, setLoggingIn] = useState(false)

  // --- Rejestracja ---
  const [reg, setReg] = useState(emptyReg)
  const [regMsg, setRegMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  // --- Zmiana e-maila (gdy zalogowany) ---
  const [newEmail, setNewEmail] = useState('')
  const [emailMsg, setEmailMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  // --- Adresy (gdy zalogowany) ---
  const [addresses, setAddresses] = useState<AddressDto[]>([])
  const [addr, setAddr] = useState(emptyAddr)
  const [addrMsg, setAddrMsg] = useState<string | null>(null)

  const loadAddresses = () => {
    if (session) getAddresses().then(setAddresses).catch(() => {})
  }

  useEffect(loadAddresses, [session])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoginMsg(null)
    setLoggingIn(true)
    try {
      await login(loginForm)
      setLoginForm(emptyLogin)
    } catch (err) {
      setLoginMsg((err as Error).message)
    } finally {
      setLoggingIn(false)
    }
  }

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setRegMsg(null)
    try {
      await registerCustomer({ ...reg, phone: reg.phone || undefined })
      setRegMsg({ type: 'success', text: 'Konto utworzone. Zaloguj się e-mailem i hasłem.' })
      setReg(emptyReg)
    } catch (err) {
      setRegMsg({ type: 'error', text: (err as Error).message })
    }
  }

  const handleChangeEmail = async (e: React.FormEvent) => {
    e.preventDefault()
    setEmailMsg(null)
    try {
      await changeEmail(newEmail)
      await refresh()
      setNewEmail('')
      setEmailMsg({ type: 'success', text: 'E-mail zmieniony.' })
    } catch (err) {
      setEmailMsg({ type: 'error', text: (err as Error).message })
    }
  }

  const handleDeactivateAddress = async (addressId: number) => {
    if (!session) return
    try {
      await deactivateAddress(addressId)
      loadAddresses()
    } catch (err) {
      setAddrMsg((err as Error).message)
    }
  }

  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!session) return
    setAddrMsg(null)
    try {
      await addAddress({ ...addr })
      setAddr(emptyAddr)
      setAddrMsg('Adres dodany.')
      loadAddresses()
    } catch (err) {
      setAddrMsg((err as Error).message)
    }
  }

  return (
    <div>
      <div className="page-head"><h1>Konto</h1></div>

      <div className="stack">
        {/* Sesja */}
        <div className="card center-narrow" style={{ width: '100%' }}>
          <h2>Sesja</h2>
          {isLoggedIn ? (
            <div className="row-between">
              <span>Zalogowano jako <strong>{session?.firstName} {session?.lastName}</strong> ({session?.email})</span>
              <button className="btn btn-danger" onClick={logout}>Wyloguj</button>
            </div>
          ) : (
            <form onSubmit={handleLogin}>
              {loginMsg && <div className="alert alert-error">{loginMsg}</div>}
              <div className="field">
                <label>E-mail</label>
                <input className="input" type="email" required value={loginForm.email}
                  onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })} />
              </div>
              <div className="field">
                <label>Hasło <span className="muted">(puste dla kont bez hasła)</span></label>
                <input className="input" type="password" value={loginForm.password}
                  onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })} />
              </div>
              <button className="btn btn-primary btn-block" type="submit" disabled={loggingIn}>
                {loggingIn ? 'Logowanie…' : 'Zaloguj'}
              </button>
            </form>
          )}
        </div>

        {/* Rejestracja */}
        {!isLoggedIn && (
          <div className="card center-narrow" style={{ width: '100%' }}>
            <h2>Rejestracja</h2>
            {regMsg && <div className={`alert alert-${regMsg.type === 'success' ? 'success' : 'error'}`}>{regMsg.text}</div>}
            <form onSubmit={handleRegister}>
              <div className="form-row">
                <div className="field">
                  <label>Imię *</label>
                  <input className="input" required value={reg.firstName}
                    onChange={(e) => setReg({ ...reg, firstName: e.target.value })} />
                </div>
                <div className="field">
                  <label>Nazwisko *</label>
                  <input className="input" required value={reg.lastName}
                    onChange={(e) => setReg({ ...reg, lastName: e.target.value })} />
                </div>
              </div>
              <div className="field">
                <label>E-mail *</label>
                <input className="input" type="email" required value={reg.email}
                  onChange={(e) => setReg({ ...reg, email: e.target.value })} />
              </div>
              <div className="field">
                <label>Telefon</label>
                <input className="input" value={reg.phone}
                  onChange={(e) => setReg({ ...reg, phone: e.target.value })} />
              </div>
              <div className="field">
                <label>Hasło *</label>
                <input className="input" type="password" required value={reg.passwordHash}
                  onChange={(e) => setReg({ ...reg, passwordHash: e.target.value })} />
              </div>
              <button className="btn btn-primary btn-block" type="submit">Załóż konto</button>
            </form>
          </div>
        )}

        {/* Zmiana e-maila */}
        {isLoggedIn && (
          <div className="card center-narrow" style={{ width: '100%' }}>
            <h2>Zmień e-mail</h2>
            {emailMsg && <div className={`alert alert-${emailMsg.type === 'success' ? 'success' : 'error'}`}>{emailMsg.text}</div>}
            <form onSubmit={handleChangeEmail}>
              <div className="field">
                <label>Nowy e-mail</label>
                <input className="input" type="email" required value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)} placeholder={session?.email} />
              </div>
              <button className="btn btn-primary btn-block" type="submit">Zmień e-mail</button>
            </form>
          </div>
        )}

        {/* Adresy */}
        {isLoggedIn && (
          <div className="card">
            <h2>Moje adresy</h2>
            {addresses.length === 0 ? (
              <p className="muted">Brak zapisanych adresów.</p>
            ) : (
              <ul style={{ paddingLeft: 18, margin: '0 0 12px' }}>
                {addresses.map((a) => (
                  <li key={a.addressId} style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                    <span>
                      {a.label ? <strong>{a.label}: </strong> : null}
                      {a.street}, {a.postalCode} {a.city}, {a.country}
                      {a.isDefault && <span className="badge badge-default" style={{ marginLeft: 8 }}>domyślny</span>}
                    </span>
                    <button
                      className="btn btn-danger"
                      style={{ padding: '2px 8px', fontSize: '0.8rem' }}
                      onClick={() => handleDeactivateAddress(a.addressId)}
                    >
                      Usuń
                    </button>
                  </li>
                ))}
              </ul>
            )}

            <h3>Dodaj adres</h3>
            {addrMsg && <div className="alert alert-info">{addrMsg}</div>}
            <form onSubmit={handleAddAddress}>
              <div className="field">
                <label>Etykieta</label>
                <input className="input" value={addr.label} onChange={(e) => setAddr({ ...addr, label: e.target.value })} />
              </div>
              <div className="field">
                <label>Ulica i numer *</label>
                <input className="input" required value={addr.street} onChange={(e) => setAddr({ ...addr, street: e.target.value })} />
              </div>
              <div className="form-row">
                <div className="field">
                  <label>Kod pocztowy *</label>
                  <input className="input" required value={addr.postalCode} onChange={(e) => setAddr({ ...addr, postalCode: e.target.value })} />
                </div>
                <div className="field">
                  <label>Miasto *</label>
                  <input className="input" required value={addr.city} onChange={(e) => setAddr({ ...addr, city: e.target.value })} />
                </div>
              </div>
              <div className="field">
                <label>Kraj *</label>
                <input className="input" required value={addr.country} onChange={(e) => setAddr({ ...addr, country: e.target.value })} />
              </div>
              <button className="btn btn-primary" type="submit">Dodaj adres</button>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { addAddress, createOrder, getAddresses } from '../api/endpoints'
import type { AddressDto } from '../api/types'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { formatPrice } from '../format'

const emptyAddress = { label: '', street: '', city: '', postalCode: '', country: 'Poland' }

export default function CheckoutPage() {
  const { session, isLoggedIn } = useAuth()
  const { items, totalPrice, clear } = useCart()
  const navigate = useNavigate()

  const [addresses, setAddresses] = useState<AddressDto[]>([])
  const [selectedAddress, setSelectedAddress] = useState<number | null>(null)
  const [showNew, setShowNew] = useState(false)
  const [newAddr, setNewAddr] = useState(emptyAddress)
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const loadAddresses = async (preselectLast = false) => {
    if (!session) return
    const list = await getAddresses()
    setAddresses(list)
    if (list.length > 0) {
      const def = list.find((a) => a.isDefault)
      setSelectedAddress(preselectLast ? list[list.length - 1].addressId : (def ?? list[0]).addressId)
    }
  }

  useEffect(() => {
    if (isLoggedIn) loadAddresses().catch((e) => setError(e.message))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn])

  if (!isLoggedIn) {
    return (
      <div className="center-narrow">
        <div className="alert alert-info">Aby złożyć zamówienie, musisz być zalogowany.</div>
        <Link to="/account" className="btn btn-primary btn-block">Przejdź do logowania</Link>
      </div>
    )
  }

  if (items.length === 0) {
    return <div className="empty">Koszyk jest pusty. <Link to="/">Wróć do produktów →</Link></div>
  }

  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!session) return
    setError(null)
    try {
      await addAddress({ ...newAddr })
      setNewAddr(emptyAddress)
      setShowNew(false)
      await loadAddresses(true)
    } catch (err) {
      setError((err as Error).message)
    }
  }

  const handleSubmit = async () => {
    if (!session || selectedAddress == null) return
    setSubmitting(true)
    setError(null)
    try {
      await createOrder({
        addressId: selectedAddress,
        items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
      })
      clear()
      navigate('/orders', { state: { justOrdered: true } })
    } catch (err) {
      setError((err as Error).message)
      setSubmitting(false)
    }
  }

  return (
    <div>
      <div className="page-head"><h1>Zamówienie</h1></div>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="stack">
        <div className="card">
          <div className="row-between" style={{ marginBottom: 12 }}>
            <h2 style={{ margin: 0 }}>Adres dostawy</h2>
            <button className="btn btn-sm" onClick={() => setShowNew((s) => !s)}>
              {showNew ? 'Anuluj' : '+ Nowy adres'}
            </button>
          </div>

          {addresses.length === 0 && !showNew && (
            <p className="muted">Brak zapisanych adresów — dodaj nowy.</p>
          )}

          {addresses.map((a) => (
            <label key={a.addressId} className="cart-line" style={{ cursor: 'pointer' }}>
              <input
                type="radio"
                name="address"
                checked={selectedAddress === a.addressId}
                onChange={() => setSelectedAddress(a.addressId)}
              />
              <span className="name">
                {a.label ? <strong>{a.label}: </strong> : null}
                {a.street}, {a.postalCode} {a.city}, {a.country}
              </span>
              {a.isDefault && <span className="badge badge-default">domyślny</span>}
            </label>
          ))}

          {showNew && (
            <form onSubmit={handleAddAddress} style={{ marginTop: 12 }}>
              <div className="field">
                <label>Etykieta (np. Dom)</label>
                <input className="input" value={newAddr.label}
                  onChange={(e) => setNewAddr({ ...newAddr, label: e.target.value })} />
              </div>
              <div className="field">
                <label>Ulica i numer *</label>
                <input className="input" required value={newAddr.street}
                  onChange={(e) => setNewAddr({ ...newAddr, street: e.target.value })} />
              </div>
              <div className="form-row">
                <div className="field">
                  <label>Kod pocztowy *</label>
                  <input className="input" required value={newAddr.postalCode}
                    onChange={(e) => setNewAddr({ ...newAddr, postalCode: e.target.value })} />
                </div>
                <div className="field">
                  <label>Miasto *</label>
                  <input className="input" required value={newAddr.city}
                    onChange={(e) => setNewAddr({ ...newAddr, city: e.target.value })} />
                </div>
              </div>
              <div className="field">
                <label>Kraj *</label>
                <input className="input" required value={newAddr.country}
                  onChange={(e) => setNewAddr({ ...newAddr, country: e.target.value })} />
              </div>
              <button className="btn btn-primary" type="submit">Zapisz adres</button>
            </form>
          )}
        </div>

        <div className="card">
          <h2>Podsumowanie</h2>
          {items.map((i) => (
            <div key={i.productId} className="summary">
              <span>{i.name} × {i.quantity}</span>
              <span>{formatPrice(i.price * i.quantity)}</span>
            </div>
          ))}
          <div className="summary total">
            <span>Razem</span>
            <span>{formatPrice(totalPrice)}</span>
          </div>
          <button
            className="btn btn-primary btn-block"
            style={{ marginTop: 16 }}
            disabled={submitting || selectedAddress == null}
            onClick={handleSubmit}
          >
            {submitting ? 'Składanie zamówienia…' : 'Złóż zamówienie'}
          </button>
          {selectedAddress == null && <p className="muted" style={{ marginTop: 8 }}>Wybierz adres dostawy.</p>}
        </div>
      </div>
    </div>
  )
}

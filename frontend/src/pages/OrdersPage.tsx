import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { cancelOrder, getOrders, getPaymentMethods, payOrder } from '../api/endpoints'
import type { OrderDto, PaymentMethod } from '../api/types'
import { useAuth } from '../context/AuthContext'
import StatusBadge from '../components/StatusBadge'
import { formatDate, formatPrice } from '../format'

export default function OrdersPage() {
  const { session, isLoggedIn } = useAuth()
  const [orders, setOrders] = useState<OrderDto[]>([])
  const [methods, setMethods] = useState<PaymentMethod[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [busyOrder, setBusyOrder] = useState<number | null>(null)
  const [selectedMethod, setSelectedMethod] = useState<Record<number, number>>({})

  const load = async () => {
    if (!session) return
    setLoading(true)
    try {
      const [o, m] = await Promise.all([getOrders(), getPaymentMethods()])
      setOrders(o)
      setMethods(m)
      setError(null)
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (isLoggedIn) load()
    else setLoading(false)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn])

  if (!isLoggedIn) {
    return (
      <div className="center-narrow">
        <div className="alert alert-info">Zaloguj się, aby zobaczyć swoje zamówienia.</div>
        <Link to="/account" className="btn btn-primary btn-block">Przejdź do logowania</Link>
      </div>
    )
  }

  const handlePay = async (orderId: number) => {
    if (!session) return
    const methodId = selectedMethod[orderId] ?? methods[0]?.paymentMethodId
    if (!methodId) { setError('Brak dostępnych metod płatności.'); return }
    setBusyOrder(orderId)
    setError(null)
    try {
      await payOrder(orderId, { paymentMethodId: methodId })
      await load()
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setBusyOrder(null)
    }
  }

  const handleCancel = async (orderId: number) => {
    if (!session) return
    setBusyOrder(orderId)
    setError(null)
    try {
      await cancelOrder(orderId)
      await load()
    } catch (e) {
      setError((e as Error).message)
    } finally {
      setBusyOrder(null)
    }
  }

  const isPending = (status: string) => status?.toLowerCase() === 'pending'
  // Anulować można zamówienia oczekujące oraz spakowane (packed) —
  // trigger w bazie przy przejściu packed -> cancelled przywraca stan magazynowy.
  const canCancel = (status: string) => ['pending', 'packed'].includes(status?.toLowerCase())

  return (
    <div>
      <div className="page-head row-between">
        <h1>Moje zamówienia</h1>
        <button className="btn btn-sm" onClick={load}>Odśwież</button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="spinner">Ładowanie…</div>
      ) : orders.length === 0 ? (
        <div className="empty">Nie masz jeszcze żadnych zamówień. <Link to="/">Zobacz produkty →</Link></div>
      ) : (
        <div className="stack">
          {orders.map((o) => (
            <div key={o.orderId} className="card">
              <div className="row-between" style={{ marginBottom: 8 }}>
                <h3 style={{ margin: 0 }}>Zamówienie #{o.orderId}</h3>
                <StatusBadge status={o.status} />
              </div>
              <p className="muted" style={{ fontSize: 13, marginBottom: 12 }}>
                Złożono: {formatDate(o.orderDate)}
                {o.shipDate && ` · Wysłano: ${formatDate(o.shipDate)}`}
                {o.address && ` · ${o.address.street}, ${o.address.postalCode} ${o.address.city}`}
              </p>

              <table className="table">
                <thead>
                  <tr><th>Produkt</th><th className="num">Cena</th><th className="num">Ilość</th><th className="num">Suma</th></tr>
                </thead>
                <tbody>
                  {o.items?.map((it) => (
                    <tr key={it.productId}>
                      <td>{it.name}</td>
                      <td className="num">{formatPrice(it.unitPrice)}</td>
                      <td className="num">{it.quantity}</td>
                      <td className="num">{formatPrice(it.total)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="summary total"><span>Razem</span><span>{formatPrice(o.totalOrderCost)}</span></div>

              {(isPending(o.status) || canCancel(o.status)) && (
                <div className="toolbar" style={{ marginTop: 12, marginBottom: 0 }}>
                  {isPending(o.status) && (
                    <>
                      <select
                        className="select"
                        style={{ maxWidth: 220 }}
                        value={selectedMethod[o.orderId] ?? methods[0]?.paymentMethodId ?? ''}
                        onChange={(e) => setSelectedMethod({ ...selectedMethod, [o.orderId]: Number(e.target.value) })}
                      >
                        {methods.map((m) => (
                          <option key={m.paymentMethodId} value={m.paymentMethodId}>
                            {m.provider} ({m.type})
                          </option>
                        ))}
                      </select>
                      <button className="btn btn-primary" disabled={busyOrder === o.orderId} onClick={() => handlePay(o.orderId)}>
                        {busyOrder === o.orderId ? '…' : 'Zapłać'}
                      </button>
                    </>
                  )}
                  {canCancel(o.status) && (
                    <button className="btn btn-danger" disabled={busyOrder === o.orderId} onClick={() => handleCancel(o.orderId)}>
                      {busyOrder === o.orderId ? '…' : 'Anuluj zamówienie'}
                    </button>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

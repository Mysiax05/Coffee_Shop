import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getPayments, getTotalExpense } from '../api/endpoints'
import type { PaymentDto } from '../api/types'
import { useAuth } from '../context/AuthContext'
import StatusBadge from '../components/StatusBadge'
import { formatDate, formatPrice } from '../format'

export default function PaymentsPage() {
  const { session, isLoggedIn } = useAuth()
  const [payments, setPayments] = useState<PaymentDto[]>([])
  const [total, setTotal] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!isLoggedIn || !session) { setLoading(false); return }
    Promise.all([getPayments(session.customerId), getTotalExpense(session.customerId)])
      .then(([p, t]) => { setPayments(p); setTotal(Number(t)) })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn])

  if (!isLoggedIn) {
    return (
      <div className="center-narrow">
        <div className="alert alert-info">Zaloguj się, aby zobaczyć historię płatności.</div>
        <Link to="/account" className="btn btn-primary btn-block">Przejdź do logowania</Link>
      </div>
    )
  }

  return (
    <div>
      <div className="page-head"><h1>Płatności</h1></div>
      {error && <div className="alert alert-error">{error}</div>}

      {total !== null && (
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="summary total" style={{ border: 'none', margin: 0, padding: 0 }}>
            <span>Łączne wydatki</span>
            <span>{formatPrice(total)}</span>
          </div>
        </div>
      )}

      {loading ? (
        <div className="spinner">Ładowanie…</div>
      ) : payments.length === 0 ? (
        <div className="empty">Brak płatności.</div>
      ) : (
        <div className="card">
          <table className="table">
            <thead>
              <tr><th>#</th><th>Zamówienie</th><th>Metoda</th><th className="num">Kwota</th><th>Status</th><th>Utworzono</th><th>Opłacono</th></tr>
            </thead>
            <tbody>
              {payments.map((p) => (
                <tr key={p.paymentId}>
                  <td>{p.paymentId}</td>
                  <td>#{p.orderId}</td>
                  <td>{p.paymentMethod ? `${p.paymentMethod.provider} (${p.paymentMethod.type})` : '—'}</td>
                  <td className="num">{formatPrice(p.amount)}</td>
                  <td><StatusBadge status={p.status} /></td>
                  <td className="muted">{formatDate(p.createdAt)}</td>
                  <td className="muted">{formatDate(p.paidAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

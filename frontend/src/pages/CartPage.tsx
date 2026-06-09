import { Link, useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { formatPrice } from '../format'

export default function CartPage() {
  const { items, totalPrice, setQty, remove, clear } = useCart()
  const navigate = useNavigate()

  if (items.length === 0) {
    return (
      <div>
        <div className="page-head"><h1>Koszyk</h1></div>
        <div className="empty">
          Twój koszyk jest pusty.<br />
          <Link to="/">Przejdź do produktów →</Link>
        </div>
      </div>
    )
  }

  return (
    <div>
      <div className="page-head row-between">
        <h1>Koszyk</h1>
        <button className="btn btn-sm" onClick={clear}>Wyczyść koszyk</button>
      </div>

      <div className="card">
        {items.map((i) => (
          <div key={i.productId} className="cart-line">
            <span className="name">{i.name}</span>
            <span className="muted">{formatPrice(i.price)}</span>
            <div className="qty">
              <button className="btn btn-sm" onClick={() => setQty(i.productId, i.quantity - 1)}>−</button>
              <input
                className="input"
                type="number"
                min={1}
                max={i.stock || undefined}
                value={i.quantity}
                onChange={(e) => setQty(i.productId, Number(e.target.value) || 1)}
              />
              <button className="btn btn-sm" onClick={() => setQty(i.productId, i.quantity + 1)}>+</button>
            </div>
            <strong style={{ minWidth: 90, textAlign: 'right' }}>{formatPrice(i.price * i.quantity)}</strong>
            <button className="btn btn-sm btn-danger" onClick={() => remove(i.productId)}>Usuń</button>
          </div>
        ))}

        <div className="summary total">
          <span>Razem</span>
          <span>{formatPrice(totalPrice)}</span>
        </div>
      </div>

      <div className="row-between" style={{ marginTop: 16 }}>
        <Link to="/" className="btn">← Kontynuuj zakupy</Link>
        <button className="btn btn-primary" onClick={() => navigate('/checkout')}>Przejdź do zamówienia →</button>
      </div>
    </div>
  )
}

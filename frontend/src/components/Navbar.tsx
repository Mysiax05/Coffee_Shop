import { NavLink, Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { totalItems } = useCart()
  const { session, isLoggedIn, logout } = useAuth()

  return (
    <nav className="nav">
      <div className="nav-inner">
        <Link to="/" className="nav-brand">🛒 Sklep</Link>
        <div className="nav-links">
          <NavLink to="/" end className="nav-link">Produkty</NavLink>
          <NavLink to="/cart" className="nav-link">
            Koszyk
            {totalItems > 0 && <span className="nav-cart-badge">{totalItems}</span>}
          </NavLink>
          <NavLink to="/orders" className="nav-link">Zamówienia</NavLink>
          <NavLink to="/payments" className="nav-link">Płatności</NavLink>
          <NavLink to="/account" className="nav-link">Konto</NavLink>
        </div>
        <div className="nav-right">
          {isLoggedIn ? (
            <>
              <span className="nav-user">Klient #{session?.customerId}</span>
              <button className="btn btn-sm" onClick={logout}>Wyloguj</button>
            </>
          ) : (
            <NavLink to="/account" className="btn btn-sm btn-primary">Zaloguj</NavLink>
          )}
        </div>
      </div>
    </nav>
  )
}

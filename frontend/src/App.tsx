import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import ProductsPage from './pages/ProductsPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrdersPage'
import PaymentsPage from './pages/PaymentsPage'
import AccountPage from './pages/AccountPage'

function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<ProductsPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/payments" element={<PaymentsPage />} />
          <Route path="/account" element={<AccountPage />} />
          <Route path="*" element={<div className="empty">Nie znaleziono strony.</div>} />
        </Routes>
      </main>
    </div>
  )
}

export default App

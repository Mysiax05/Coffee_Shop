import { useEffect, useMemo, useState } from 'react'
import { getCategories, getProducts } from '../api/endpoints'
import type { CategoryDto, ProductDto } from '../api/types'
import { useCart } from '../context/CartContext'
import { formatPrice } from '../format'

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductDto[]>([])
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [search, setSearch] = useState('')
  const [categoryId, setCategoryId] = useState<number | 'all'>('all')
  const { add } = useCart()

  useEffect(() => {
    Promise.all([getProducts(), getCategories()])
      .then(([p, c]) => {
        setProducts(p)
        setCategories(c)
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  const filtered = useMemo(() => {
    return products.filter((p) => {
      const matchCat = categoryId === 'all' || p.categoryId === categoryId
      const matchSearch = p.name.toLowerCase().includes(search.toLowerCase())
      return matchCat && matchSearch
    })
  }, [products, categoryId, search])

  return (
    <div>
      <div className="page-head">
        <h1>Produkty</h1>
        <p className="sub">Przeglądaj ofertę i dodawaj do koszyka</p>
      </div>

      {error && <div className="alert alert-error">Nie udało się pobrać danych: {error}</div>}

      <div className="toolbar">
        <input
          className="input grow"
          placeholder="Szukaj produktu…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <select
          className="select"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value === 'all' ? 'all' : Number(e.target.value))}
        >
          <option value="all">Wszystkie kategorie</option>
          {categories.map((c) => (
            <option key={c.categoryId} value={c.categoryId}>{c.categoryName}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="spinner">Ładowanie…</div>
      ) : filtered.length === 0 ? (
        <div className="empty">Brak produktów spełniających kryteria.</div>
      ) : (
        <div className="grid">
          {filtered.map((p) => {
            const outOfStock = p.stock <= 0
            return (
              <div key={p.productId} className="card product-card">
                <span className="cat">{p.categoryName}</span>
                <span className="name">{p.name}</span>
                <span className="price">{formatPrice(p.price)}</span>
                <span className="stock">{outOfStock ? 'Brak na stanie' : `Na stanie: ${p.stock}`}</span>
                <span className="spacer" />
                <button
                  className="btn btn-primary btn-block"
                  disabled={outOfStock}
                  onClick={() => add(p)}
                >
                  Dodaj do koszyka
                </button>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

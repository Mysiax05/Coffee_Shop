import { useEffect, useMemo, useState } from 'react'
import {
  filterProducts,
  getBestSellerByCategory,
  getCategories,
  getProducts,
  getTopBestSellers,
} from '../api/endpoints'
import type { BestSellerDto, CategoryDto, ProductDto } from '../api/types'
import CategoryCascader from '../components/CategoryCascader'
import ProductCard from '../components/ProductCard'

// Zwraca ID kategorii oraz wszystkich jej podkategorii (poddrzewo) na podstawie listy kategorii.
function subtreeIds(categories: CategoryDto[], rootId: number): Set<number> {
  const ids = new Set<number>([rootId])
  let changed = true
  while (changed) {
    changed = false
    for (const c of categories) {
      if (c.parentCategoryId != null && ids.has(c.parentCategoryId) && !ids.has(c.categoryId)) {
        ids.add(c.categoryId)
        changed = true
      }
    }
  }
  return ids
}

function parsePrice(value: string): number | null {
  const t = value.trim()
  if (t === '') return null
  const n = Number(t)
  return Number.isFinite(n) ? n : null
}

export default function ProductsPage() {
  const [allProducts, setAllProducts] = useState<ProductDto[]>([])
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [topBest, setTopBest] = useState<BestSellerDto[]>([])
  const [bestByCat, setBestByCat] = useState<Record<string, BestSellerDto>>({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [search, setSearch] = useState('')
  const [categoryPath, setCategoryPath] = useState<number[]>([])
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  // Wybrane filtry atrybutow: klucz -> wartosc zakodowana w JSON (zachowuje typ: 8 vs "8").
  const [attrFilters, setAttrFilters] = useState<Record<string, string>>({})
  // Wynik filtrowania z serwera. null = pokazujemy pelna liste produktow.
  const [results, setResults] = useState<ProductDto[] | null>(null)
  const [filtering, setFiltering] = useState(false)

  const activeCategoryId = categoryPath.length ? categoryPath[categoryPath.length - 1] : null
  const activeCategoryName =
    activeCategoryId != null
      ? categories.find((c) => c.categoryId === activeCategoryId)?.categoryName ?? null
      : null

  useEffect(() => {
    Promise.all([getProducts(), getCategories(), getTopBestSellers(), getBestSellerByCategory()])
      .then(([p, c, top, byCat]) => {
        setAllProducts(p)
        setCategories(c)
        setTopBest(top)
        setBestByCat(byCat)
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  const runFilter = (
    categoryId: number | null,
    min: number | null,
    max: number | null,
    attrs: Record<string, unknown>,
  ) => {
    setFiltering(true)
    setError(null)
    filterProducts({
      categoryId,
      minPrice: min,
      maxPrice: max,
      attributes: Object.keys(attrs).length ? JSON.stringify(attrs) : null,
    })
      .then(setResults)
      .catch((e) => setError(e.message))
      .finally(() => setFiltering(false))
  }

  // Zmiana kategorii: resetujemy filtry atrybutow (sa specyficzne dla kategorii)
  // i pobieramy produkty z poddrzewa wybranej kategorii (lub pelna liste dla "wszystkie").
  useEffect(() => {
    setAttrFilters({})
    if (activeCategoryId == null) {
      setResults(null)
      return
    }
    runFilter(activeCategoryId, parsePrice(minPrice), parsePrice(maxPrice), {})
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeCategoryId])

  // Dostepne atrybuty (klucz -> mozliwe wartosci) dla aktywnej kategorii.
  const attrOptions = useMemo(() => {
    if (activeCategoryId == null) return {} as Record<string, Map<string, string>>
    const sub = subtreeIds(categories, activeCategoryId)
    const map: Record<string, Map<string, string>> = {}
    for (const p of allProducts) {
      if (p.categoryId == null || !sub.has(p.categoryId)) continue
      let obj: Record<string, unknown>
      try {
        obj = JSON.parse(p.attributes || '{}')
      } catch {
        continue
      }
      for (const [k, v] of Object.entries(obj)) {
        if (!map[k]) map[k] = new Map()
        map[k].set(JSON.stringify(v), String(v)) // klucz mapy = wartosc w JSON, label = tekst
      }
    }
    return map
  }, [allProducts, categories, activeCategoryId])

  const applyFilters = () => {
    const attrs: Record<string, unknown> = {}
    for (const [k, enc] of Object.entries(attrFilters)) {
      if (enc) attrs[k] = JSON.parse(enc)
    }
    runFilter(activeCategoryId, parsePrice(minPrice), parsePrice(maxPrice), attrs)
  }

  const clearFilters = () => {
    setMinPrice('')
    setMaxPrice('')
    setAttrFilters({})
    setCategoryPath([]) // wywola efekt -> results = null
  }

  const list = results ?? allProducts
  const visible = list.filter((p) => p.name.toLowerCase().includes(search.toLowerCase()))

  const renderBestSeller = (bs: BestSellerDto) => {
    const product = allProducts.find((p) => p.productId === bs.productId)
    if (product) {
      return (
        <ProductCard
          key={bs.productId}
          product={product}
          badge="🏆 Bestseller"
          note={`Sprzedano: ${bs.totalSold} szt.`}
        />
      )
    }
    // Produkt moze byc nieaktywny / spoza listy aktywnych — pokazujemy bez koszyka.
    return (
      <div key={bs.productId} className="card product-card">
        <span className="bestseller-badge">🏆 Bestseller</span>
        <span className="name">{bs.name}</span>
        <span className="muted" style={{ fontSize: 13 }}>Sprzedano: {bs.totalSold} szt.</span>
        <span className="spacer" />
        <button className="btn btn-block" disabled>Produkt niedostępny</button>
      </div>
    )
  }

  const hasAttrFilters = Object.keys(attrOptions).length > 0

  return (
    <div>
      <div className="page-head">
        <h1>Produkty</h1>
        <p className="sub">Przeglądaj ofertę i dodawaj do koszyka</p>
      </div>

      {error && <div className="alert alert-error">Wystąpił błąd: {error}</div>}

      {/* Bestsellery */}
      {!loading && (
        <>
          {activeCategoryId == null && topBest.length > 0 && (
            <section style={{ marginBottom: 24 }}>
              <h2>🏆 Bestsellery</h2>
              <div className="grid">{topBest.map(renderBestSeller)}</div>
            </section>
          )}
          {activeCategoryId != null && activeCategoryName && bestByCat[activeCategoryName] && (
            <section style={{ marginBottom: 24 }}>
              <h2>🏆 Bestseller w kategorii „{activeCategoryName}”</h2>
              <div className="grid">{renderBestSeller(bestByCat[activeCategoryName])}</div>
            </section>
          )}
        </>
      )}

      {/* Filtry */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div className="field" style={{ marginBottom: 12 }}>
          <label>Kategoria</label>
          <CategoryCascader categories={categories} path={categoryPath} onChange={setCategoryPath} />
        </div>

        <div className="form-row">
          <div className="field">
            <label>Szukaj</label>
            <input
              className="input"
              placeholder="Nazwa produktu…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <div className="field">
            <label>Cena min</label>
            <input className="input" type="number" min={0} value={minPrice}
              onChange={(e) => setMinPrice(e.target.value)} placeholder="0" />
          </div>
          <div className="field">
            <label>Cena max</label>
            <input className="input" type="number" min={0} value={maxPrice}
              onChange={(e) => setMaxPrice(e.target.value)} placeholder="∞" />
          </div>
        </div>

        {hasAttrFilters && (
          <div className="form-row">
            {Object.entries(attrOptions).map(([key, values]) => (
              <div className="field" key={key}>
                <label>{key}</label>
                <select
                  className="select"
                  value={attrFilters[key] ?? ''}
                  onChange={(e) => setAttrFilters({ ...attrFilters, [key]: e.target.value })}
                >
                  <option value="">dowolny</option>
                  {[...values.entries()].map(([enc, label]) => (
                    <option key={enc} value={enc}>{label}</option>
                  ))}
                </select>
              </div>
            ))}
          </div>
        )}

        <div className="toolbar" style={{ marginBottom: 0, marginTop: 4 }}>
          <button className="btn btn-primary" disabled={filtering} onClick={applyFilters}>
            {filtering ? 'Filtruję…' : 'Filtruj'}
          </button>
          <button className="btn" onClick={clearFilters}>Wyczyść</button>
        </div>
      </div>

      {loading ? (
        <div className="spinner">Ładowanie…</div>
      ) : visible.length === 0 ? (
        <div className="empty">Brak produktów spełniających kryteria.</div>
      ) : (
        <div className="grid">
          {visible.map((p) => (
            <ProductCard key={p.productId} product={p} />
          ))}
        </div>
      )}
    </div>
  )
}

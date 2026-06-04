import type { ReactNode } from 'react'
import type { ProductDto } from '../api/types'
import { useCart } from '../context/CartContext'
import { formatPrice } from '../format'

interface Props {
  product: ProductDto
  badge?: ReactNode
  note?: ReactNode
}

// Czytelne wyswietlenie atrybutow JSON, np. {"roast":"medium"} -> "roast: medium"
function attrsText(attributes: string | null | undefined): string {
  if (!attributes) return ''
  try {
    const obj = JSON.parse(attributes) as Record<string, unknown>
    return Object.entries(obj)
      .map(([k, v]) => `${k}: ${v}`)
      .join(' · ')
  } catch {
    return ''
  }
}

export default function ProductCard({ product, badge, note }: Props) {
  const { add } = useCart()
  const outOfStock = product.stock <= 0
  const attrs = attrsText(product.attributes)

  return (
    <div className="card product-card">
      {badge && <span className="bestseller-badge">{badge}</span>}
      {product.categoryName && <span className="cat">{product.categoryName}</span>}
      <span className="name">{product.name}</span>
      <span className="price">{formatPrice(product.price)}</span>
      {attrs && <span className="attrs">{attrs}</span>}
      <span className="stock">{outOfStock ? 'Brak na stanie' : `Na stanie: ${product.stock}`}</span>
      {note && <span className="muted" style={{ fontSize: 13 }}>{note}</span>}
      <span className="spacer" />
      <button className="btn btn-primary btn-block" disabled={outOfStock} onClick={() => add(product)}>
        Dodaj do koszyka
      </button>
    </div>
  )
}

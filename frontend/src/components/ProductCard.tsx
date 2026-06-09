import type { ReactNode } from 'react'
import type { ProductDto } from '../api/types'
import { useCart } from '../context/CartContext'
import { formatPrice } from '../format'
import ProductImage from './ProductImage'

interface Props {
  product: ProductDto
  badge?: ReactNode
  note?: ReactNode
}

export default function ProductCard({ product, badge, note }: Props) {
  const { add } = useCart()
  const outOfStock = product.stock <= 0

  return (
    <div className="card product-card">
      <div className="product-img-wrap">
        <ProductImage productId={product.productId} alt={product.name} />
        {badge && <span className="bestseller-badge product-img-badge">{badge}</span>}
      </div>
      {product.categoryName && <span className="cat">{product.categoryName}</span>}
      <span className="name">{product.name}</span>
      <span className="price">{formatPrice(product.price)}</span>
      <span className="stock">{outOfStock ? 'Brak na stanie' : `Na stanie: ${product.stock}`}</span>
      {note && <span className="muted" style={{ fontSize: 13 }}>{note}</span>}
      <span className="spacer" />
      <button className="btn btn-primary btn-block" disabled={outOfStock} onClick={() => add(product)}>
        Dodaj do koszyka
      </button>
    </div>
  )
}

import { useState } from 'react'

// Obrazki produktow Spring serwuje ze static/images jako /images/{id}.{ext}.
// Rozszerzenia sa rozne (.webp/.jpg), wiec probujemy kolejno; gdy zadne nie zadziala
// pokazujemy placeholder.
const EXTENSIONS = ['webp', 'jpg', 'jpeg', 'png']

interface Props {
  productId: number
  alt: string
}

export default function ProductImage({ productId, alt }: Props) {
  const [idx, setIdx] = useState(0)
  const [failed, setFailed] = useState(false)

  if (failed) {
    return <div className="product-img product-img-placeholder" aria-label="brak zdjęcia">☕</div>
  }

  return (
    <img
      className="product-img"
      src={`/images/${productId}.${EXTENSIONS[idx]}`}
      alt={alt}
      loading="lazy"
      onError={() => (idx < EXTENSIONS.length - 1 ? setIdx(idx + 1) : setFailed(true))}
    />
  )
}

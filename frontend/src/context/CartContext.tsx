import { createContext, useContext, useEffect, useMemo, useReducer } from 'react'
import type { ReactNode } from 'react'
import type { ProductDto } from '../api/types'

// Koszyk w pelni po stronie React (useReducer + Context), trwaly w localStorage.

export interface CartItem {
  productId: number
  name: string
  price: number
  stock: number
  quantity: number
}

type Action =
  | { type: 'ADD'; product: ProductDto }
  | { type: 'REMOVE'; productId: number }
  | { type: 'SET_QTY'; productId: number; quantity: number }
  | { type: 'CLEAR' }

function clampQty(qty: number, stock: number): number {
  if (qty < 1) return 1
  if (stock > 0 && qty > stock) return stock
  return qty
}

function reducer(state: CartItem[], action: Action): CartItem[] {
  switch (action.type) {
    case 'ADD': {
      const p = action.product
      const existing = state.find((i) => i.productId === p.productId)
      if (existing) {
        return state.map((i) =>
          i.productId === p.productId
            ? { ...i, quantity: clampQty(i.quantity + 1, i.stock) }
            : i,
        )
      }
      return [
        ...state,
        { productId: p.productId, name: p.name, price: Number(p.price), stock: p.stock, quantity: 1 },
      ]
    }
    case 'REMOVE':
      return state.filter((i) => i.productId !== action.productId)
    case 'SET_QTY':
      return state.map((i) =>
        i.productId === action.productId
          ? { ...i, quantity: clampQty(action.quantity, i.stock) }
          : i,
      )
    case 'CLEAR':
      return []
    default:
      return state
  }
}

interface CartContextValue {
  items: CartItem[]
  totalItems: number
  totalPrice: number
  add: (product: ProductDto) => void
  remove: (productId: number) => void
  setQty: (productId: number, quantity: number) => void
  clear: () => void
}

const CartContext = createContext<CartContextValue | null>(null)
const STORAGE_KEY = 'shop.cart'

function init(): CartItem[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as CartItem[]) : []
  } catch {
    return []
  }
}

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, dispatch] = useReducer(reducer, [], init)

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items))
  }, [items])

  const value = useMemo<CartContextValue>(() => {
    const totalItems = items.reduce((s, i) => s + i.quantity, 0)
    const totalPrice = items.reduce((s, i) => s + i.price * i.quantity, 0)
    return {
      items,
      totalItems,
      totalPrice,
      add: (product) => dispatch({ type: 'ADD', product }),
      remove: (productId) => dispatch({ type: 'REMOVE', productId }),
      setQty: (productId, quantity) => dispatch({ type: 'SET_QTY', productId, quantity }),
      clear: () => dispatch({ type: 'CLEAR' }),
    }
  }, [items])

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart musi byc uzyte wewnatrz CartProvider')
  return ctx
}

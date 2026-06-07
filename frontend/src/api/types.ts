// Typy TS odpowiadajace DTO z backendu (com.dbproject.backend.dto / entity).
// Uwaga na serializacje Jackson:
//  - ProductDto.isActive (boolean prymityw) -> w JSON pole "active"
//  - AddressDto.isDefault / PaymentMethod.isActive (Boolean) -> pola "isDefault" / "isActive"

export interface ProductDto {
  productId: number
  name: string
  // Uwaga: odpowiedz z POST /products/filter NIE zawiera kategorii (sa null/0).
  categoryId: number | null
  categoryName: string | null
  price: number
  stock: number
  active?: boolean
  attributes: string
}

// Cialo POST /api/products/filter (ProductFilterRequest).
// attributes = JSON jako string, np. '{"roast":"medium"}' (dopasowanie przez zawieranie).
export interface ProductFilterRequest {
  minPrice?: number | null
  maxPrice?: number | null
  categoryId?: number | null
  attributes?: string | null
}

// GET /api/reports/best-sellers (BestSellerDto).
export interface BestSellerDto {
  productId: number
  name: string
  totalSold: number
  revenue: number
}

export interface CategoryDto {
  categoryId: number
  categoryName: string
  parentCategoryId: number | null
  parentCategoryName: string | null
}

export interface AddressDto {
  addressId: number
  label: string | null
  street: string
  city: string
  postalCode: string
  country: string
  isDefault: boolean
}

export interface AddAddressRequest {
  label: string
  street: string
  city: string
  postalCode: string
  country: string
}

export interface OrderItem {
  productId: number
  name: string
  quantity: number
  unitPrice: number
  total: number
}

export interface AddressSummaryDto {
  addressId: number
  street: string
  city: string
  postalCode: string
  country: string
}

export interface OrderDto {
  orderId: number
  customerId: number
  address: AddressSummaryDto
  status: string
  orderDate: string
  shipDate: string | null
  items: OrderItem[]
  totalOrderCost: number
}

export interface CreateOrderItem {
  productId: number
  quantity: number
}

export interface CreateOrderRequest {
  addressId: number
  items: CreateOrderItem[]
}

export interface PayOrderRequest {
  paymentMethodId: number
}

export interface PaymentMethod {
  paymentMethodId: number
  provider: string
  type: string
  isActive: boolean
}

export interface PaymentDto {
  paymentId: number
  orderId: number
  orderStatus: string
  paymentMethod: {
    paymentMethodId: number
    provider: string
    type: string
  }
  amount: number
  status: string
  createdAt: string
  paidAt: string | null
}

// Cialo rejestracji = encja Customer (haslo trafia do pola passwordHash).
export interface RegisterCustomerRequest {
  firstName: string
  lastName: string
  email: string
  phone?: string
  passwordHash: string
}

// Cialo POST /api/auth/login. Haslo opcjonalne - konta bez hasla loguja sie samym e-mailem.
export interface LoginRequest {
  email: string
  password?: string
}

// Odpowiedz /api/auth/login oraz /api/auth/me (CustomerDto, bez hasla).
export interface CustomerDto {
  customerId: number
  firstName: string
  lastName: string
  email: string
  phone: string | null
}

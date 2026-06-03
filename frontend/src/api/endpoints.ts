// Funkcje wywolujace konkretne endpointy backendu (katalog backend/.../web).
import { api } from './client'
import type {
  AddAddressRequest,
  AddressDto,
  CategoryDto,
  CreateOrderRequest,
  OrderDto,
  PayOrderRequest,
  PaymentDto,
  PaymentMethod,
  ProductDto,
  RegisterCustomerRequest,
} from './types'

// --- Produkty ---  ProductController
export const getProducts = () => api.get<ProductDto[]>('/products')
export const getProduct = (id: number) => api.get<ProductDto>(`/products/${id}`)

// --- Kategorie ---  CategoryController
export const getCategories = () => api.get<CategoryDto[]>('/categories')
// Zwraca plaska liste kategorii: wskazana kategoria + wszystkie jej podkategorie (poddrzewo).
export const getCategorySubtree = (id: number) =>
  api.get<CategoryDto[]>(`/categories/${id}`)

// --- Klienci ---  CustomerController (tylko rejestracja; loginu brak w backendzie)
export const registerCustomer = (body: RegisterCustomerRequest) =>
  api.post<void>('/customers', body)

// --- Adresy ---  AddressController
export const getAddresses = (customerId: number) =>
  api.get<AddressDto[]>(`/addresses/customer/${customerId}`)
export const addAddress = (body: AddAddressRequest) => api.post<void>('/addresses', body)

// --- Zamowienia ---  OrderController
export const getOrders = (customerId: number) =>
  api.get<OrderDto[]>(`/orders/customer/${customerId}`)
export const createOrder = (body: CreateOrderRequest) => api.post<void>('/orders', body)
export const payOrder = (orderId: number, body: PayOrderRequest) =>
  api.post<void>(`/orders/${orderId}/pay`, body)
export const cancelOrder = (orderId: number, customerId: number) =>
  api.post<void>(`/orders/${orderId}/cancel?customerId=${customerId}`)

// --- Platnosci ---  PaymentController
export const getPayments = (customerId: number) =>
  api.get<PaymentDto[]>(`/payments/customer/${customerId}`)
export const getTotalExpense = (customerId: number) =>
  api.get<number>(`/payments/customer/${customerId}/total`)

// --- Metody platnosci ---  PaymentMethodController
export const getPaymentMethods = () => api.get<PaymentMethod[]>('/paymentmethods')

// Funkcje wywolujace konkretne endpointy backendu (katalog backend/.../web).
import { api } from './client'
import type {
  AddAddressRequest,
  AddressDto,
  BestSellerDto,
  CategoryDto,
  CreateOrderRequest,
  CustomerDto,
  LoginRequest,
  OrderDto,
  PayOrderRequest,
  PaymentDto,
  PaymentMethod,
  ProductDto,
  ProductFilterRequest,
  RegisterCustomerRequest,
} from './types'

// --- Produkty ---  ProductController
export const getProducts = () => api.get<ProductDto[]>('/products')
export const getProduct = (id: number) => api.get<ProductDto>(`/products/${id}`)
// Filtruje aktywne produkty po cenie min/max, kategorii (z poddrzewem) i atrybutach.
export const filterProducts = (body: ProductFilterRequest) =>
  api.post<ProductDto[]>('/products/filter', body)

// --- Raporty / bestsellery ---  ReportController
// 3 najlepiej sprzedajace sie produkty (globalnie).
export const getTopBestSellers = () => api.get<BestSellerDto[]>('/reports/best-sellers')
// Mapa: nazwa kategorii -> 1 bestseller z jej poddrzewa.
export const getBestSellerByCategory = () =>
  api.get<Record<string, BestSellerDto>>('/reports/best-sellers/by-category')

// --- Kategorie ---  CategoryController
export const getCategories = () => api.get<CategoryDto[]>('/categories')
// Zwraca plaska liste kategorii: wskazana kategoria + wszystkie jej podkategorie (poddrzewo).
export const getCategorySubtree = (id: number) =>
  api.get<CategoryDto[]>(`/categories/${id}`)

// --- Klienci ---  CustomerController (rejestracja, zmiana e-maila)
export const registerCustomer = (body: RegisterCustomerRequest) =>
  api.post<void>('/customers', body)
export const changeEmail = (newEmail: string) =>
  api.patch<void>('/customers/changemail', { newEmail })

// --- Logowanie ---  AuthController (sesja HTTP oparta o ciasteczko)
export const login = (body: LoginRequest) => api.post<CustomerDto>('/auth/login', body)
export const logout = () => api.post<void>('/auth/logout')
export const getMe = () => api.get<CustomerDto>('/auth/me')

// --- Adresy ---  AddressController (klient brany z sesji)
export const getAddresses = () => api.get<AddressDto[]>('/addresses')
export const addAddress = (body: AddAddressRequest) => api.post<void>('/addresses', body)
export const deactivateAddress = (addressId: number) =>
  api.patch<void>(`/addresses/${addressId}/deactivate`)

// --- Zamowienia ---  OrderController (klient brany z sesji)
export const getOrders = () => api.get<OrderDto[]>('/orders')
export const createOrder = (body: CreateOrderRequest) => api.post<void>('/orders', body)
export const payOrder = (orderId: number, body: PayOrderRequest) =>
  api.post<void>(`/orders/${orderId}/pay`, body)
export const cancelOrder = (orderId: number) =>
  api.post<void>(`/orders/${orderId}/cancel`)

// --- Platnosci ---  PaymentController (klient brany z sesji)
export const getPayments = () => api.get<PaymentDto[]>('/payments')
export const getTotalExpense = () => api.get<number>('/payments/total')

// --- Metody platnosci ---  PaymentMethodController
export const getPaymentMethods = () => api.get<PaymentMethod[]>('/paymentmethods')

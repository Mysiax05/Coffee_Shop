export const formatPrice = (value: number | string | null | undefined): string => {
  const n = Number(value ?? 0)
  return new Intl.NumberFormat('pl-PL', { style: 'currency', currency: 'PLN' }).format(n)
}

export const formatDate = (value: string | null | undefined): string => {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return String(value)
  return d.toLocaleString('pl-PL', { dateStyle: 'short', timeStyle: 'short' })
}

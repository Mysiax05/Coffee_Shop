// Mapuje status zamowienia/platnosci na kolor odznaki.
const MAP: Record<string, string> = {
  pending: 'badge-pending',
  paid: 'badge-paid',
  completed: 'badge-completed',
  shipped: 'badge-success',
  cancelled: 'badge-cancelled',
  canceled: 'badge-cancelled',
  failed: 'badge-failed',
}

export default function StatusBadge({ status }: { status: string }) {
  const key = (status ?? '').toLowerCase()
  const cls = MAP[key] ?? 'badge-default'
  return <span className={`badge ${cls}`}>{status}</span>
}

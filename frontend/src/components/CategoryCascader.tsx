import type { CategoryDto } from '../api/types'

interface Props {
  categories: CategoryDto[]
  // Sciezka wybranych kategorii od korzenia, np. [Kawa, Kawa ziarnista, Arabica].
  path: number[]
  onChange: (path: number[]) => void
}

// Rozwijane menu odzwierciedlajace drzewo kategorii: kolejny poziom pojawia sie
// dopiero po wyborze kategorii nadrzednej (Kawa -> Kawa ziarnista -> Arabica).
export default function CategoryCascader({ categories, path, onChange }: Props) {
  const childrenOf = (parentId: number | null) =>
    categories.filter((c) => (c.parentCategoryId ?? null) === parentId)

  // Budujemy listy rozwijane: poziom 0 = kategorie glowne, kolejne = dzieci wyboru.
  const levels: { options: CategoryDto[]; selected: number | '' }[] = []
  let options = childrenOf(null)
  let i = 0
  while (options.length > 0) {
    const selected: number | '' = i < path.length ? path[i] : ''
    levels.push({ options, selected })
    if (selected === '') break
    options = childrenOf(selected)
    i++
  }

  const handle = (levelIdx: number, raw: string) => {
    const next = path.slice(0, levelIdx)
    if (raw) next.push(Number(raw))
    onChange(next)
  }

  return (
    <div className="cascader">
      {levels.map((lvl, idx) => (
        <select
          key={idx}
          className="select"
          value={lvl.selected}
          onChange={(e) => handle(idx, e.target.value)}
        >
          <option value="">{idx === 0 ? 'Wszystkie kategorie' : '— wybierz podkategorie —'}</option>
          {lvl.options.map((c) => (
            <option key={c.categoryId} value={c.categoryId}>
              {c.categoryName}
            </option>
          ))}
        </select>
      ))}
    </div>
  )
}

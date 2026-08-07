export default function Paginacija({ stranica, ukupnoStranica, promeni }) {
  if (ukupnoStranica <= 1) return null

  const brojevi = []
  const od = Math.max(0, stranica - 2)
  const doo = Math.min(ukupnoStranica - 1, stranica + 2)

  for (let i = od; i <= doo; i++) {
    brojevi.push(i)
  }

  return (
    <nav>
      <ul className="pagination justify-content-center">
        <li className={`page-item ${stranica === 0 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => promeni(stranica - 1)}>
            Prethodna
          </button>
        </li>

        {od > 0 && (
          <li className="page-item">
            <button className="page-link" onClick={() => promeni(0)}>1</button>
          </li>
        )}
        {od > 1 && (
          <li className="page-item disabled">
            <span className="page-link">…</span>
          </li>
        )}

        {brojevi.map(i => (
          <li key={i} className={`page-item ${i === stranica ? 'active' : ''}`}>
            <button className="page-link" onClick={() => promeni(i)}>
              {i + 1}
            </button>
          </li>
        ))}

        {doo < ukupnoStranica - 2 && (
          <li className="page-item disabled">
            <span className="page-link">…</span>
          </li>
        )}
        {doo < ukupnoStranica - 1 && (
          <li className="page-item">
            <button className="page-link" onClick={() => promeni(ukupnoStranica - 1)}>
              {ukupnoStranica}
            </button>
          </li>
        )}

        <li className={`page-item ${stranica >= ukupnoStranica - 1 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => promeni(stranica + 1)}>
            Sledeća
          </button>
        </li>
      </ul>
    </nav>
  )
}
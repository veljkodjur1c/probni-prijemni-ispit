import { useEffect, useState } from 'react'
import { prijaveApi, terminiApi } from '../../api/servisi'

export default function SpisakPoTerminu() {
  const [termini, setTermini] = useState([])
  const [prijave, setPrijave] = useState([])
  const [terminId, setTerminId] = useState('')
  const [ucitava, setUcitava] = useState(true)

  useEffect(() => {
    ucitaj()
  }, [])

  const ucitaj = async () => {
    try {
      const [odgTermini, odgPrijave] = await Promise.all([
        terminiApi.svi(),
        prijaveApi.sve('PRIJAVLJEN')
      ])
      setTermini(odgTermini.data)
      setPrijave(odgPrijave.data)
    } finally {
      setUcitava(false)
    }
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  const kandidati = terminId
    ? prijave
        .filter(p => p.stavke.some(s => s.termin.id === Number(terminId)))
        .map(p => ({ ime: p.imeKandidata, prijavaId: p.id }))
    : []

  if (ucitava) {
    return <div className="container">Učitavanje...</div>
  }

  return (
    <div className="container">
      <h3 className="mb-4">Spisak prijavljenih kandidata</h3>

      <div className="row mb-4">
        <div className="col-md-6">
          <label className="form-label">Termin</label>
          <select
            className="form-select"
            value={terminId}
            onChange={(e) => setTerminId(e.target.value)}
          >
            <option value="">Izaberite termin</option>
            {termini.map(t => (
              <option key={t.id} value={t.id}>
                {new Date(t.datum).toLocaleDateString('sr-RS')} u {t.vremePocetka.substring(0, 5)}
                {' — '}{nazivVrste(t.vrstaIspita)}
              </option>
            ))}
          </select>
        </div>
      </div>

      {!terminId ? (
        <div className="alert alert-secondary">
          Izaberite termin da biste videli spisak kandidata.
        </div>
      ) : kandidati.length === 0 ? (
        <div className="alert alert-info">
          Nema prijavljenih kandidata za ovaj termin.
        </div>
      ) : (
        <>
          <p className="text-muted">Broj kandidata: {kandidati.length}</p>
          <table className="table table-hover">
            <thead>
              <tr>
                <th style={{ width: '60px' }}>#</th>
                <th>Kandidat</th>
                <th>Prijava</th>
              </tr>
            </thead>
            <tbody>
              {kandidati.map((k, i) => (
                <tr key={k.prijavaId}>
                  <td>{i + 1}</td>
                  <td>{k.ime}</td>
                  <td>#{k.prijavaId}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  )
}
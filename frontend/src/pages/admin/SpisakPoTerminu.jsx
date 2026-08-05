import { useEffect, useState } from 'react'
import { prijaveApi, terminiApi, izvozApi } from '../../api/servisi'
import { sacuvajFajl } from '../../api/preuzimanje'

export default function SpisakPoTerminu() {
  const [termini, setTermini] = useState([])
  const [prijave, setPrijave] = useState([])
  const [terminId, setTerminId] = useState('')
  const [greska, setGreska] = useState('')
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
    } catch {
      setGreska('Greška pri učitavanju podataka')
    } finally {
      setUcitava(false)
    }
  }

  const izvezi = async () => {
    setGreska('')
    try {
      const odgovor = await izvozApi.spisak(terminId)
      sacuvajFajl(odgovor.data, `spisak-termin-${terminId}.xlsx`)
    } catch {
      setGreska('Greška pri izvozu spiska')
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

      {greska && <div className="alert alert-danger">{greska}</div>}

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
          <div className="d-flex justify-content-between align-items-center mb-2">
            <p className="text-muted mb-0">Broj kandidata: {kandidati.length}</p>
            <button className="btn btn-outline-success btn-sm" onClick={izvezi}>
              Izvezi u Excel
            </button>
          </div>

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
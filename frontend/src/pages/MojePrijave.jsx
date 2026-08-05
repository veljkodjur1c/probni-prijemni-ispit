import { useEffect, useState } from 'react'
import { prijaveApi } from '../api/servisi'

export default function MojePrijave() {
  const [prijave, setPrijave] = useState([])
  const [greska, setGreska] = useState('')
  const [ucitava, setUcitava] = useState(true)

  useEffect(() => {
    ucitaj()
  }, [])

  const ucitaj = async () => {
    try {
      const odgovor = await prijaveApi.moje()
      setPrijave(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju prijava')
    } finally {
      setUcitava(false)
    }
  }

  const otkazi = async (id) => {
    if (!confirm('Da li ste sigurni da želite da otkažete prijavu?')) return

    setGreska('')
    try {
      await prijaveApi.otkaziMoju(id)
      ucitaj()
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri otkazivanju')
    }
  }

  const oznaka = (status) => {
    const mapa = {
      NA_CEKANJU: ['bg-warning text-dark', 'Na čekanju'],
      PRIJAVLJEN: ['bg-success', 'Prijavljen'],
      OTKAZANA: ['bg-secondary', 'Otkazana']
    }
    const [klasa, tekst] = mapa[status] || ['bg-light', status]
    return <span className={`badge ${klasa}`}>{tekst}</span>
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  if (ucitava) return <div className="container">Učitavanje...</div>

  return (
    <div className="container">
      <h3 className="mb-4">Moje prijave</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}

      {prijave.length === 0 ? (
        <div className="alert alert-info">Nemate nijednu prijavu.</div>
      ) : (
        prijave.map(p => (
          <div className="card mb-3" key={p.id}>
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <div>
                  <h5 className="mb-1">Prijava #{p.id}</h5>
                  <small className="text-muted">
                    {new Date(p.datumPrijave).toLocaleString('sr-RS')}
                  </small>
                </div>
                {oznaka(p.status)}
              </div>

              <table className="table table-sm mb-3">
                <tbody>
                  {p.stavke.map(s => (
                    <tr key={s.termin.id}>
                      <td>{new Date(s.termin.datum).toLocaleDateString('sr-RS')}</td>
                      <td>{s.termin.vremePocetka.substring(0, 5)}</td>
                      <td>{nazivVrste(s.termin.vrstaIspita)}</td>
                      <td className="text-end">{s.cena} RSD</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="d-flex justify-content-between align-items-center">
                <span className="fs-5">Ukupno: <strong>{p.ukupnaCena} RSD</strong></span>
                {p.status === 'NA_CEKANJU' && (
                  <button className="btn btn-outline-danger btn-sm" onClick={() => otkazi(p.id)}>
                    Otkaži prijavu
                  </button>
                )}
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  )
}
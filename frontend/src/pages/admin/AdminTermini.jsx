import { useEffect, useState } from 'react'
import { terminiApi } from '../../api/servisi'

const prazanFormular = {
  datum: '',
  vremePocetka: '',
  vrstaIspita: 'MATEMATIKA',
  adresa: ''
}

export default function AdminTermini() {
  const [termini, setTermini] = useState([])
  const [formular, setFormular] = useState(prazanFormular)
  const [izmenaId, setIzmenaId] = useState(null)
  const [greska, setGreska] = useState('')
  const [poruka, setPoruka] = useState('')
  const [greskePolja, setGreskePolja] = useState({})
  const [ucitava, setUcitava] = useState(true)
  const [salje, setSalje] = useState(false)

  useEffect(() => {
    ucitaj()
  }, [])

  const ucitaj = async () => {
    try {
      const odgovor = await terminiApi.svi()
      setTermini(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju termina')
    } finally {
      setUcitava(false)
    }
  }

  const promeni = (e) => {
    setFormular({ ...formular, [e.target.name]: e.target.value })
  }

  const ocisti = () => {
    setFormular(prazanFormular)
    setIzmenaId(null)
    setGreskePolja({})
  }

  const pripremiIzmenu = (t) => {
    setFormular({
      datum: t.datum,
      vremePocetka: t.vremePocetka.substring(0, 5),
      vrstaIspita: t.vrstaIspita,
      adresa: t.adresa
    })
    setIzmenaId(t.id)
    setGreska('')
    setPoruka('')
    setGreskePolja({})
  }

  const posalji = async (e) => {
    e.preventDefault()
    setGreska('')
    setPoruka('')
    setGreskePolja({})
    setSalje(true)

    const podaci = {
      ...formular,
      vremePocetka: formular.vremePocetka.length === 5
        ? `${formular.vremePocetka}:00`
        : formular.vremePocetka
    }

    try {
      if (izmenaId) {
        await terminiApi.izmeni(izmenaId, podaci)
        setPoruka('Termin je izmenjen')
      } else {
        await terminiApi.kreiraj(podaci)
        setPoruka('Termin je dodat')
      }
      ocisti()
      ucitaj()
    } catch (err) {
      const odgovor = err.response?.data
      setGreska(odgovor?.poruka || 'Greška pri čuvanju termina')
      setGreskePolja(odgovor?.greskePolja || {})
    } finally {
      setSalje(false)
    }
  }

  const obrisi = async (id) => {
    if (!confirm('Obrisati ovaj termin?')) return

    setGreska('')
    setPoruka('')
    try {
      await terminiApi.obrisi(id)
      setPoruka('Termin je obrisan')
      ucitaj()
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri brisanju')
    }
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  if (ucitava) {
    return <div className="container">Učitavanje...</div>
  }

  return (
    <div className="container">
      <h3 className="mb-4">Upravljanje terminima</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}
      {poruka && <div className="alert alert-success">{poruka}</div>}

      <div className="row">
        <div className="col-lg-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title mb-3">
                {izmenaId ? `Izmena termina #${izmenaId}` : 'Novi termin'}
              </h5>

              <form onSubmit={posalji}>
                <div className="mb-3">
                  <label className="form-label">Datum</label>
                  <input
                    type="date"
                    name="datum"
                    className={`form-control ${greskePolja.datum ? 'is-invalid' : ''}`}
                    value={formular.datum}
                    onChange={promeni}
                    required
                  />
                  {greskePolja.datum && (
                    <div className="invalid-feedback">{greskePolja.datum}</div>
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Vreme početka</label>
                  <input
                    type="time"
                    name="vremePocetka"
                    className="form-control"
                    value={formular.vremePocetka}
                    onChange={promeni}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Vrsta ispita</label>
                  <select
                    name="vrstaIspita"
                    className="form-select"
                    value={formular.vrstaIspita}
                    onChange={promeni}
                  >
                    <option value="MATEMATIKA">Matematika</option>
                    <option value="OPSTA_INFORMISANOST">Test opšte informisanosti</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label">Adresa</label>
                  <input
                    type="text"
                    name="adresa"
                    className={`form-control ${greskePolja.adresa ? 'is-invalid' : ''}`}
                    value={formular.adresa}
                    onChange={promeni}
                    required
                  />
                  {greskePolja.adresa && (
                    <div className="invalid-feedback">{greskePolja.adresa}</div>
                  )}
                </div>

                <button type="submit" className="btn btn-primary w-100 mb-2" disabled={salje}>
                  {salje ? 'Čuvanje...' : izmenaId ? 'Sačuvaj izmene' : 'Dodaj termin'}
                </button>

                {izmenaId && (
                  <button type="button" className="btn btn-outline-secondary w-100" onClick={ocisti}>
                    Odustani
                  </button>
                )}
              </form>
            </div>
          </div>
        </div>

        <div className="col-lg-8">
          {termini.length === 0 ? (
            <div className="alert alert-info">Nema unetih termina.</div>
          ) : (
            <table className="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Datum</th>
                  <th>Vreme</th>
                  <th>Vrsta ispita</th>
                  <th>Adresa</th>
                  <th className="text-end">Akcije</th>
                </tr>
              </thead>
              <tbody>
                {termini.map(t => (
                  <tr key={t.id}>
                    <td>{new Date(t.datum).toLocaleDateString('sr-RS')}</td>
                    <td>{t.vremePocetka.substring(0, 5)}</td>
                    <td>{nazivVrste(t.vrstaIspita)}</td>
                    <td>{t.adresa}</td>
                    <td className="text-end">
                      <button
                        className="btn btn-outline-primary btn-sm me-2"
                        onClick={() => pripremiIzmenu(t)}
                      >
                        Izmeni
                      </button>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => obrisi(t.id)}
                      >
                        Obriši
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
import { useEffect, useState } from 'react'
import { cenovnikApi } from '../../api/servisi'

const prazanFormular = {
  vrstaIspita: 'MATEMATIKA',
  cena: '',
  vaziOd: ''
}

export default function AdminCenovnik() {
  const [cene, setCene] = useState([])
  const [formular, setFormular] = useState(prazanFormular)
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
      const odgovor = await cenovnikApi.sve()
      setCene(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju cenovnika')
    } finally {
      setUcitava(false)
    }
  }

  const promeni = (e) => {
    setFormular({ ...formular, [e.target.name]: e.target.value })
  }

  const posalji = async (e) => {
    e.preventDefault()
    setGreska('')
    setPoruka('')
    setGreskePolja({})
    setSalje(true)

    try {
      await cenovnikApi.postavi(formular)
      setPoruka('Cena je postavljena')
      setFormular(prazanFormular)
      ucitaj()
    } catch (err) {
      const odgovor = err.response?.data
      setGreska(odgovor?.poruka || 'Greška pri postavljanju cene')
      setGreskePolja(odgovor?.greskePolja || {})
    } finally {
      setSalje(false)
    }
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  const danas = new Date().toISOString().split('T')[0]

  const vaziDanas = (c) => {
    const isteVrste = cene
      .filter(x => x.vrstaIspita === c.vrstaIspita && x.vaziOd <= danas)
      .sort((a, b) => b.vaziOd.localeCompare(a.vaziOd))
    return isteVrste.length > 0 && isteVrste[0].id === c.id
  }

  const sortirane = [...cene].sort((a, b) => {
    if (a.vrstaIspita !== b.vrstaIspita) {
      return a.vrstaIspita.localeCompare(b.vrstaIspita)
    }
    return b.vaziOd.localeCompare(a.vaziOd)
  })

  if (ucitava) {
    return <div className="container">Učitavanje...</div>
  }

  return (
    <div className="container">
      <h3 className="mb-4">Cenovnik</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}
      {poruka && <div className="alert alert-success">{poruka}</div>}

      <div className="row">
        <div className="col-lg-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title mb-3">Nova cena</h5>

              <form onSubmit={posalji}>
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
                  <label className="form-label">Cena (RSD)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    name="cena"
                    className={`form-control ${greskePolja.cena ? 'is-invalid' : ''}`}
                    value={formular.cena}
                    onChange={promeni}
                    required
                  />
                  {greskePolja.cena && (
                    <div className="invalid-feedback">{greskePolja.cena}</div>
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Važi od</label>
                  <input
                    type="date"
                    name="vaziOd"
                    className={`form-control ${greskePolja.vaziOd ? 'is-invalid' : ''}`}
                    value={formular.vaziOd}
                    onChange={promeni}
                    required
                  />
                  {greskePolja.vaziOd && (
                    <div className="invalid-feedback">{greskePolja.vaziOd}</div>
                  )}
                </div>

                <button type="submit" className="btn btn-primary w-100" disabled={salje}>
                  {salje ? 'Čuvanje...' : 'Postavi cenu'}
                </button>
              </form>

              <p className="text-muted small mt-3 mb-0">
                Postojeće cene se ne menjaju. Nova cena važi od zadatog datuma,
                a već podnete prijave zadržavaju cenu po kojoj su napravljene.
              </p>
            </div>
          </div>
        </div>

        <div className="col-lg-8">
          {sortirane.length === 0 ? (
            <div className="alert alert-info">Cenovnik je prazan.</div>
          ) : (
            <table className="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Vrsta ispita</th>
                  <th>Cena</th>
                  <th>Važi od</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {sortirane.map(c => (
                  <tr key={c.id}>
                    <td>{nazivVrste(c.vrstaIspita)}</td>
                    <td>{c.cena} RSD</td>
                    <td>{new Date(c.vaziOd).toLocaleDateString('sr-RS')}</td>
                    <td>
                      {vaziDanas(c) && (
                        <span className="badge bg-success">Trenutno važi</span>
                      )}
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
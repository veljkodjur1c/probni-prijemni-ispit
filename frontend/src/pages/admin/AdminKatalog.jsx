import { useEffect, useState } from 'react'
import { katalogApi } from '../../api/servisi'

const VRSTE = [
  { vrednost: 'MATEMATIKA', naziv: 'Matematika' },
  { vrednost: 'OPSTA_INFORMISANOST', naziv: 'Test opšte informisanosti' }
]

const prazanFormular = {
  naziv: '',
  vaziOd: '',
  cene: { MATEMATIKA: '', OPSTA_INFORMISANOST: '' }
}

export default function AdminKatalog() {
  const [katalozi, setKatalozi] = useState([])
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
      const odgovor = await katalogApi.svi()
      setKatalozi(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju kataloga')
    } finally {
      setUcitava(false)
    }
  }

  const promeniPolje = (e) => {
    setFormular({ ...formular, [e.target.name]: e.target.value })
  }

  const promeniCenu = (vrsta, vrednost) => {
    setFormular({
      ...formular,
      cene: { ...formular.cene, [vrsta]: vrednost }
    })
  }

  const posalji = async (e) => {
    e.preventDefault()
    setGreska('')
    setPoruka('')
    setGreskePolja({})
    setSalje(true)

    const stavke = VRSTE
      .filter(v => formular.cene[v.vrednost] !== '')
      .map(v => ({
        vrstaIspita: v.vrednost,
        cena: Number(formular.cene[v.vrednost])
      }))

    if (stavke.length === 0) {
      setGreska('Unesite bar jednu cenu')
      setSalje(false)
      return
    }

    try {
      await katalogApi.kreiraj({
        naziv: formular.naziv,
        vaziOd: formular.vaziOd,
        stavke
      })
      setPoruka('Katalog je kreiran')
      setFormular(prazanFormular)
      ucitaj()
    } catch (err) {
      const odgovor = err.response?.data
      setGreska(odgovor?.poruka || 'Greška pri kreiranju kataloga')
      setGreskePolja(odgovor?.greskePolja || {})
    } finally {
      setSalje(false)
    }
  }

  const obrisi = async (id) => {
    if (!confirm('Obrisati ovaj katalog?')) return

    setGreska('')
    setPoruka('')
    try {
      await katalogApi.obrisi(id)
      setPoruka('Katalog je obrisan')
      ucitaj()
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri brisanju')
    }
  }

  const nazivVrste = (v) =>
    VRSTE.find(x => x.vrednost === v)?.naziv || v

  const uBuducnosti = (datum) => new Date(datum) > new Date()

  if (ucitava) {
    return <div className="container">Učitavanje...</div>
  }

  return (
    <div className="container">
      <h3 className="mb-4">Katalozi cena</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}
      {poruka && <div className="alert alert-success">{poruka}</div>}

      <div className="row">
        <div className="col-lg-4 mb-4">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title mb-3">Nov katalog</h5>

              <form onSubmit={posalji}>
                <div className="mb-3">
                  <label className="form-label">Naziv</label>
                  <input
                    type="text"
                    name="naziv"
                    className={`form-control ${greskePolja.naziv ? 'is-invalid' : ''}`}
                    placeholder="npr. Cenovnik 2027"
                    value={formular.naziv}
                    onChange={promeniPolje}
                    required
                  />
                  {greskePolja.naziv && (
                    <div className="invalid-feedback">{greskePolja.naziv}</div>
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Važi od</label>
                  <input
                    type="date"
                    name="vaziOd"
                    className={`form-control ${greskePolja.vaziOd ? 'is-invalid' : ''}`}
                    value={formular.vaziOd}
                    onChange={promeniPolje}
                    required
                  />
                  {greskePolja.vaziOd && (
                    <div className="invalid-feedback">{greskePolja.vaziOd}</div>
                  )}
                </div>

                <hr />

                {VRSTE.map(v => (
                  <div className="mb-3" key={v.vrednost}>
                    <label className="form-label">{v.naziv}</label>
                    <div className="input-group">
                      <input
                        type="number"
                        step="0.01"
                        min="0"
                        className="form-control"
                        value={formular.cene[v.vrednost]}
                        onChange={(e) => promeniCenu(v.vrednost, e.target.value)}
                      />
                      <span className="input-group-text">RSD</span>
                    </div>
                  </div>
                ))}

                <button type="submit" className="btn btn-primary w-100" disabled={salje}>
                  {salje ? 'Čuvanje...' : 'Kreiraj katalog'}
                </button>
              </form>

              <p className="text-muted small mt-3 mb-0">
                Postojeći katalozi se ne menjaju. Nova cena važi od zadatog datuma,
                a već podnete prijave zadržavaju cenu po kojoj su napravljene.
              </p>
            </div>
          </div>
        </div>

        <div className="col-lg-8">
          {katalozi.length === 0 ? (
            <div className="alert alert-info">Nema unetih kataloga.</div>
          ) : (
            katalozi.map(k => (
              <div className="card mb-3" key={k.id}>
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 className="mb-1">
                        {k.naziv}
                        {k.vaziDanas && (
                          <span className="badge bg-success ms-2">Trenutno važi</span>
                        )}
                        {uBuducnosti(k.vaziOd) && (
                          <span className="badge bg-secondary ms-2">Budući</span>
                        )}
                      </h5>
                      <small className="text-muted">
                        Važi od {new Date(k.vaziOd).toLocaleDateString('sr-RS')}
                      </small>
                    </div>

                    {uBuducnosti(k.vaziOd) && (
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => obrisi(k.id)}
                      >
                        Obriši
                      </button>
                    )}
                  </div>

                  <table className="table table-sm mb-0">
                    <tbody>
                      {k.stavke.map(s => (
                        <tr key={s.id}>
                          <td>{nazivVrste(s.vrstaIspita)}</td>
                          <td className="text-end">{s.cena} RSD</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
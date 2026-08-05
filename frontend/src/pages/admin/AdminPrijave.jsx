import { useEffect, useState } from 'react'
import { prijaveApi, uplateApi } from '../../api/servisi'

export default function AdminPrijave() {
  const [prijave, setPrijave] = useState([])
  const [status, setStatus] = useState('')
  const [pretraga, setPretraga] = useState('')
  const [greska, setGreska] = useState('')
  const [poruka, setPoruka] = useState('')
  const [ucitava, setUcitava] = useState(true)

  useEffect(() => {
    ucitaj()
  }, [status])

  const ucitaj = async () => {
    setUcitava(true)
    try {
      const odgovor = await prijaveApi.sve(status || undefined)
      setPrijave(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju prijava')
    } finally {
      setUcitava(false)
    }
  }

  const evidentiraj = async (id) => {
    if (!confirm(`Evidentirati uplatu za prijavu #${id}?`)) return

    setGreska('')
    setPoruka('')
    try {
      await uplateApi.evidentiraj(id)
      setPoruka(`Uplata za prijavu #${id} je evidentirana`)
      ucitaj()
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri evidentiranju uplate')
    }
  }

  const otkazi = async (id) => {
    if (!confirm(`Otkazati prijavu #${id}?`)) return

    setGreska('')
    setPoruka('')
    try {
      await prijaveApi.otkaziKaoAdmin(id)
      setPoruka(`Prijava #${id} je otkazana`)
      ucitaj()
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri otkazivanju')
    }
  }

  const oznaka = (s) => {
    const mapa = {
      NA_CEKANJU: ['bg-warning text-dark', 'Na čekanju'],
      PRIJAVLJEN: ['bg-success', 'Prijavljen'],
      OTKAZANA: ['bg-secondary', 'Otkazana']
    }
    const [klasa, tekst] = mapa[s] || ['bg-light text-dark', s]
    return <span className={`badge ${klasa}`}>{tekst}</span>
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  const filtrirane = prijave.filter(p =>
    p.imeKandidata.toLowerCase().includes(pretraga.toLowerCase())
  )

  return (
    <div className="container">
      <h3 className="mb-4">Prijave kandidata</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}
      {poruka && <div className="alert alert-success">{poruka}</div>}

      <div className="row mb-4">
        <div className="col-md-4">
          <label className="form-label">Status</label>
          <select
            className="form-select"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="">Sve prijave</option>
            <option value="NA_CEKANJU">Na čekanju</option>
            <option value="PRIJAVLJEN">Prijavljen</option>
            <option value="OTKAZANA">Otkazana</option>
          </select>
        </div>

        <div className="col-md-5">
          <label className="form-label">Pretraga po kandidatu</label>
          <input
            type="text"
            className="form-control"
            placeholder="Ime ili prezime"
            value={pretraga}
            onChange={(e) => setPretraga(e.target.value)}
          />
        </div>
      </div>

      {ucitava ? (
        <div>Učitavanje...</div>
      ) : filtrirane.length === 0 ? (
        <div className="alert alert-info">Nema prijava za zadate kriterijume.</div>
      ) : (
        <>
          <p className="text-muted">Ukupno prijava: {filtrirane.length}</p>

          {filtrirane.map(p => (
            <div className="card mb-3" key={p.id}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <h5 className="mb-1">
                      Prijava #{p.id} — {p.imeKandidata}
                    </h5>
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
                        <td>{s.termin.adresa}</td>
                        <td className="text-end">{s.cena} RSD</td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <div className="d-flex justify-content-between align-items-center">
                  <span className="fs-5">
                    Ukupno: <strong>{p.ukupnaCena} RSD</strong>
                  </span>

                  {p.status === 'NA_CEKANJU' && (
                    <div>
                      <button
                        className="btn btn-success btn-sm me-2"
                        onClick={() => evidentiraj(p.id)}
                      >
                        Evidentiraj uplatu
                      </button>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => otkazi(p.id)}
                      >
                        Otkaži
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </>
      )}
    </div>
  )
}
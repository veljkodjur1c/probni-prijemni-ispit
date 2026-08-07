import { useEffect, useState } from 'react'
import { prijaveApi, uplateApi, uplatniceApi } from '../../api/servisi'
import { sacuvajFajl } from '../../api/preuzimanje'
import Paginacija from '../../components/Paginacija'

const VELICINA = 5

export default function AdminPrijave() {
  const [stranicaPodataka, setStranicaPodataka] = useState(null)
  const [status, setStatus] = useState('')
  const [pretraga, setPretraga] = useState('')
  const [primenjenaPretraga, setPrimenjenaPretraga] = useState('')
  const [stranica, setStranica] = useState(0)
  const [sortiraj, setSortiraj] = useState('datumPrijave')
  const [smer, setSmer] = useState('desc')
  const [greska, setGreska] = useState('')
  const [poruka, setPoruka] = useState('')
  const [ucitava, setUcitava] = useState(true)

  useEffect(() => {
    ucitaj()
  }, [status, primenjenaPretraga, stranica, sortiraj, smer])

  const ucitaj = async () => {
    setUcitava(true)
    try {
      const odgovor = await prijaveApi.sve({
        status: status || undefined,
        pretraga: primenjenaPretraga || undefined,
        stranica,
        velicina: VELICINA,
        sortiraj,
        smer
      })
      setStranicaPodataka(odgovor.data)
    } catch {
      setGreska('Greška pri učitavanju prijava')
    } finally {
      setUcitava(false)
    }
  }

  const promeniStatus = (noviStatus) => {
    setStatus(noviStatus)
    setStranica(0)
  }

  const pretraziSada = (e) => {
    e.preventDefault()
    setPrimenjenaPretraga(pretraga)
    setStranica(0)
  }

  const promeniSortiranje = (polje) => {
    if (sortiraj === polje) {
      setSmer(smer === 'asc' ? 'desc' : 'asc')
    } else {
      setSortiraj(polje)
      setSmer('desc')
    }
    setStranica(0)
  }

  const strelica = (polje) => {
    if (sortiraj !== polje) return ''
    return smer === 'asc' ? ' ↑' : ' ↓'
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

  const preuzmiUplatnicu = async (id) => {
    setGreska('')
    try {
      const odgovor = await uplatniceApi.preuzmi(id)
      sacuvajFajl(odgovor.data, `uplatnica-${id}.pdf`)
    } catch {
      setGreska('Greška pri preuzimanju uplatnice')
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

  const prijave = stranicaPodataka?.sadrzaj ?? []

  return (
    <div className="container">
      <h3 className="mb-4">Prijave kandidata</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}
      {poruka && <div className="alert alert-success">{poruka}</div>}

      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <label className="form-label">Status</label>
          <select
            className="form-select"
            value={status}
            onChange={(e) => promeniStatus(e.target.value)}
          >
            <option value="">Sve prijave</option>
            <option value="NA_CEKANJU">Na čekanju</option>
            <option value="PRIJAVLJEN">Prijavljen</option>
            <option value="OTKAZANA">Otkazana</option>
          </select>
        </div>

        <div className="col-md-5">
          <label className="form-label">Pretraga po kandidatu</label>
          <form onSubmit={pretraziSada} className="d-flex">
            <input
              type="text"
              className="form-control me-2"
              placeholder="Ime ili prezime"
              value={pretraga}
              onChange={(e) => setPretraga(e.target.value)}
            />
            <button type="submit" className="btn btn-outline-secondary">
              Traži
            </button>
          </form>
        </div>

        <div className="col-md-4">
          <label className="form-label">Sortiraj po</label>
          <div className="btn-group w-100">
            <button
              className={`btn btn-sm ${sortiraj === 'datumPrijave' ? 'btn-secondary' : 'btn-outline-secondary'}`}
              onClick={() => promeniSortiranje('datumPrijave')}
            >
              Datumu{strelica('datumPrijave')}
            </button>
            <button
              className={`btn btn-sm ${sortiraj === 'ukupnaCena' ? 'btn-secondary' : 'btn-outline-secondary'}`}
              onClick={() => promeniSortiranje('ukupnaCena')}
            >
              Ceni{strelica('ukupnaCena')}
            </button>
            <button
              className={`btn btn-sm ${sortiraj === 'id' ? 'btn-secondary' : 'btn-outline-secondary'}`}
              onClick={() => promeniSortiranje('id')}
            >
              Broju{strelica('id')}
            </button>
          </div>
        </div>
      </div>

      {ucitava ? (
        <div>Učitavanje...</div>
      ) : prijave.length === 0 ? (
        <div className="alert alert-info">Nema prijava za zadate kriterijume.</div>
      ) : (
        <>
          <p className="text-muted">
            Prikazano {prijave.length} od ukupno {stranicaPodataka.ukupnoElemenata} prijava
          </p>

          {prijave.map(p => (
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

                <div className="d-flex justify-content-between align-items-center flex-wrap gap-2">
                  <span className="fs-5">
                    Ukupno: <strong>{p.ukupnaCena} RSD</strong>
                  </span>

                  <div>
                    {p.status !== 'OTKAZANA' && (
                      <button
                        className="btn btn-outline-secondary btn-sm me-2"
                        onClick={() => preuzmiUplatnicu(p.id)}
                      >
                        Uplatnica
                      </button>
                    )}

                    {p.status === 'NA_CEKANJU' && (
                      <>
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
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}

          <Paginacija
            stranica={stranicaPodataka.brojStranice}
            ukupnoStranica={stranicaPodataka.ukupnoStranica}
            promeni={setStranica}
          />
        </>
      )}
    </div>
  )
}
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { terminiApi, prijaveApi, cenovnikApi } from '../api/servisi'
import { useAuth } from '../context/AuthContext'

export default function Termini() {
  const [termini, setTermini] = useState([])
  const [izabrani, setIzabrani] = useState([])
  const [cene, setCene] = useState({})
  const [greska, setGreska] = useState('')
  const [ucitava, setUcitava] = useState(true)
  const [salje, setSalje] = useState(false)

  const { korisnik, jeAdmin } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    ucitaj()
  }, [])

  const ucitaj = async () => {
    try {
      const [odgTermini, odgCene] = await Promise.all([
        terminiApi.buduci(),
        cenovnikApi.sve()
      ])

      setTermini(odgTermini.data)

      const mapa = {}
      odgCene.data.forEach(c => { mapa[c.vrstaIspita] = c.cena })
      setCene(mapa)
    } catch {
      setGreska('Greška pri učitavanju termina')
    } finally {
      setUcitava(false)
    }
  }

  const prebaci = (id) => {
    setIzabrani(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    )
  }

  const ukupno = izabrani.reduce((zbir, id) => {
    const t = termini.find(x => x.id === id)
    return zbir + (t ? Number(cene[t.vrstaIspita] || 0) : 0)
  }, 0)

  const posalji = async () => {
    setGreska('')
    setSalje(true)
    try {
      await prijaveApi.kreiraj(izabrani)
      navigate('/moje-prijave')
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri prijavi')
    } finally {
      setSalje(false)
    }
  }

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  const moZeDaPrijavi = korisnik && !jeAdmin

  if (ucitava) {
    return <div className="container">Učitavanje...</div>
  }

  return (
    <div className="container">
      <h3 className="mb-4">Raspoloživi termini</h3>

      {greska && <div className="alert alert-danger">{greska}</div>}

      {!korisnik && (
        <div className="alert alert-secondary">
          Prijavite se na sistem da biste mogli da prijavite termine.
        </div>
      )}

      {termini.length === 0 ? (
        <div className="alert alert-info">Trenutno nema raspoloživih termina.</div>
      ) : (
        <div className="row">
          <div className={moZeDaPrijavi ? 'col-lg-8' : 'col-12'}>
            <table className="table table-hover align-middle">
              <thead>
                <tr>
                  {moZeDaPrijavi && <th style={{ width: '40px' }}></th>}
                  <th>Datum</th>
                  <th>Vreme</th>
                  <th>Vrsta ispita</th>
                  <th>Adresa</th>
                  <th className="text-end">Cena</th>
                </tr>
              </thead>
              <tbody>
                {termini.map(t => (
                  <tr key={t.id}>
                    {moZeDaPrijavi && (
                      <td>
                        <input
                          type="checkbox"
                          className="form-check-input"
                          checked={izabrani.includes(t.id)}
                          onChange={() => prebaci(t.id)}
                        />
                      </td>
                    )}
                    <td>{new Date(t.datum).toLocaleDateString('sr-RS')}</td>
                    <td>{t.vremePocetka.substring(0, 5)}</td>
                    <td>{nazivVrste(t.vrstaIspita)}</td>
                    <td>{t.adresa}</td>
                    <td className="text-end">{cene[t.vrstaIspita]} RSD</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {moZeDaPrijavi && (
            <div className="col-lg-4">
              <div className="card">
                <div className="card-body">
                  <h5 className="card-title mb-3">Vaša prijava</h5>

                  {izabrani.length === 0 ? (
                    <p className="text-muted mb-0">Izaberite bar jedan termin.</p>
                  ) : (
                    <>
                      <p className="mb-2">Izabrano termina: {izabrani.length}</p>
                      <p className="fs-5 mb-3">
                        Ukupno: <strong>{ukupno} RSD</strong>
                      </p>
                      <button
                        className="btn btn-primary w-100"
                        onClick={posalji}
                        disabled={salje}
                      >
                        {salje ? 'Slanje...' : 'Prijavi se'}
                      </button>
                    </>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
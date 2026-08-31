import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { terminiApi, katalogApi } from '../api/servisi'
import { useAuth } from '../context/AuthContext'

export default function Pocetna() {
  const { korisnik, jeAdmin } = useAuth()
  const [termini, setTermini] = useState([])
  const [cene, setCene] = useState({})

  useEffect(() => {
    Promise.all([terminiApi.buduci(), katalogApi.vazeci()])
      .then(([t, k]) => {
        setTermini(t.data.slice(0, 3))
        const mapa = {}
        k.data.stavke.forEach(s => { mapa[s.vrstaIspita] = s.cena })
        setCene(mapa)
      })
      .catch(() => {})
  }, [])

  const nazivVrste = (v) =>
    v === 'MATEMATIKA' ? 'Matematika' : 'Test opšte informisanosti'

  const korak = (broj, naslov, tekst) => (
    <div className="col-md-4 mb-4">
      <div className="d-flex">
        <div
          className="flex-shrink-0 d-flex align-items-center justify-content-center rounded-circle bg-primary text-white fw-bold"
          style={{ width: '38px', height: '38px' }}
        >
          {broj}
        </div>
        <div className="ms-3">
          <h6 className="mb-1">{naslov}</h6>
          <p className="text-muted small mb-0">{tekst}</p>
        </div>
      </div>
    </div>
  )

  return (
    <div className="container">
      <div className="row align-items-center mb-5">
        <div className="col-lg-7">
          <h1 className="display-6 mb-3">Probni prijemni ispit</h1>
          <p className="fs-5 text-muted mb-4">
            Fakultet organizacionih nauka organizuje probne prijemne ispite iz
            matematike i testa opšte informisanosti. Prijavite se onlajn,
            izaberite termine koji vam odgovaraju i proverite koliko ste
            spremni za pravi prijemni.
          </p>

          {!korisnik ? (
            <>
              <Link to="/registracija" className="btn btn-primary btn-lg me-2">
                Registruj se
              </Link>
              <Link to="/termini" className="btn btn-outline-secondary btn-lg">
                Pogledaj termine
              </Link>
            </>
          ) : (
            <Link
              to={jeAdmin ? '/admin/prijave' : '/termini'}
              className="btn btn-primary btn-lg"
            >
              {jeAdmin ? 'Pregled prijava' : 'Prijavi termin'}
            </Link>
          )}
        </div>

        <div className="col-lg-5 mt-4 mt-lg-0">
          <div className="card">
            <div className="card-header bg-white">
              <h6 className="mb-0">Naredni termini</h6>
            </div>
            <ul className="list-group list-group-flush">
              {termini.length === 0 ? (
                <li className="list-group-item text-muted small">
                  Trenutno nema objavljenih termina.
                </li>
              ) : (
                termini.map(t => (
                  <li key={t.id} className="list-group-item">
                    <div className="d-flex justify-content-between align-items-center">
                      <div>
                        <div className="fw-medium">
                          {new Date(t.datum).toLocaleDateString('sr-RS')}
                          {' u '}
                          {t.vremePocetka.substring(0, 5)}
                        </div>
                        <small className="text-muted">
                          {nazivVrste(t.vrstaIspita)}
                        </small>
                      </div>
                      <span className="badge bg-light text-dark">
                        {cene[t.vrstaIspita]} RSD
                      </span>
                    </div>
                  </li>
                ))
              )}
            </ul>
            {termini.length > 0 && (
              <div className="card-footer bg-white text-center">
                <Link to="/termini" className="small text-decoration-none">
                  Svi termini
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>

      <hr className="mb-4" />

      <h5 className="mb-4">Kako funkcioniše</h5>

      <div className="row">
        {korak(1, 'Izaberite termine',
          'Pregledajte raspoložive termine i izaberite jedan ili više. Ukupna cena se računa automatski.')}
        {korak(2, 'Uplatite',
          'Nakon prijave dobijate uplatnicu na email. Možete je preuzeti i iz pregleda svojih prijava.')}
        {korak(3, 'Dolazite na ispit',
          'Kada uplata bude evidentirana, prijava dobija status „Prijavljen" i nalazite se na spisku kandidata.')}
      </div>
    </div>
  )
}
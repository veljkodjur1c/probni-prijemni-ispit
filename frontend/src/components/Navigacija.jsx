import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navigacija() {
  const { korisnik, jeAdmin, odjaviSe } = useAuth()
  const navigate = useNavigate()

  const odjava = () => {
    odjaviSe()
    navigate('/prijava')
  }

  return (
    <nav className="navbar navbar-expand navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">Probni prijemni</Link>

        <ul className="navbar-nav me-auto">
          <li className="nav-item">
            <Link className="nav-link" to="/termini">Termini</Link>
          </li>

          {korisnik && !jeAdmin && (
            <li className="nav-item">
              <Link className="nav-link" to="/moje-prijave">Moje prijave</Link>
            </li>
          )}

          {jeAdmin && (
            <>
              <li className="nav-item">
                <Link className="nav-link" to="/admin/prijave">Prijave</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/admin/cenovnik">Cenovnik</Link>
              </li>
            </>
          )}
        </ul>

        <ul className="navbar-nav">
          {korisnik ? (
            <>
              <li className="nav-item">
                <span className="nav-link">{korisnik.ime} {korisnik.prezime}</span>
              </li>
              <li className="nav-item">
                <button className="btn btn-outline-light btn-sm mt-1" onClick={odjava}>
                  Odjavi se
                </button>
              </li>
            </>
          ) : (
            <>
              <li className="nav-item">
                <Link className="nav-link" to="/prijava">Prijava</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/registracija">Registracija</Link>
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  )
}
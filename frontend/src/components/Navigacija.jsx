import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navigacija() {
  const { korisnik, jeAdmin, odjaviSe } = useAuth()
  const navigate = useNavigate()
  const lokacija = useLocation()

  const odjava = () => {
    odjaviSe()
    navigate('/prijava')
  }

  const aktivna = (putanja) =>
    lokacija.pathname === putanja ? 'nav-link active' : 'nav-link'

  const zatvoriMeni = () => {
    const meni = document.getElementById('glavniMeni')
    if (meni?.classList.contains('show')) {
      meni.classList.remove('show')
    }
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">
          Probni prijemni ispit
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#glavniMeni"
          aria-controls="glavniMeni"
          aria-expanded="false"
          aria-label="Prikaži meni"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="glavniMeni">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className={aktivna('/termini')} to="/termini" onClick={zatvoriMeni}>
                Termini
              </Link>
            </li>

            {korisnik && !jeAdmin && (
              <li className="nav-item">
                <Link className={aktivna('/moje-prijave')} to="/moje-prijave" onClick={zatvoriMeni}>
                  Moje prijave
                </Link>
              </li>
            )}

            {jeAdmin && (
              <li className="nav-item dropdown">
                <button
                  className="nav-link dropdown-toggle btn btn-link"
                  type="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  Administracija
                </button>
                <ul className="dropdown-menu">
                  <li>
                    <Link className="dropdown-item" to="/admin/prijave" onClick={zatvoriMeni}>
                      Prijave kandidata
                    </Link>
                  </li>
                  <li>
                    <Link className="dropdown-item" to="/admin/spisak" onClick={zatvoriMeni}>
                      Spisak po terminu
                    </Link>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <Link className="dropdown-item" to="/admin/termini" onClick={zatvoriMeni}>
                      Upravljanje terminima
                    </Link>
                  </li>
                  <li>
                    <Link className="dropdown-item" to="/admin/cenovnik" onClick={zatvoriMeni}>
                      Cenovnik
                    </Link>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <Link className="dropdown-item" to="/admin/statistika" onClick={zatvoriMeni}>
                      Statistika
                    </Link>
                  </li>
                </ul>
              </li>
            )}
          </ul>

          <ul className="navbar-nav align-items-lg-center">
            {korisnik ? (
              <>
                <li className="nav-item">
                  <span className="navbar-text me-lg-3">
                    {korisnik.ime} {korisnik.prezime}
                    {jeAdmin && (
                      <span className="badge bg-secondary ms-2">Admin</span>
                    )}
                  </span>
                </li>
                <li className="nav-item">
                  <button className="btn btn-outline-light btn-sm" onClick={odjava}>
                    Odjavi se
                  </button>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link className={aktivna('/prijava')} to="/prijava" onClick={zatvoriMeni}>
                    Prijava
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="btn btn-light btn-sm ms-lg-2" to="/registracija" onClick={zatvoriMeni}>
                    Registruj se
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  )
}
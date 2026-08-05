import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Registracija() {
  const [podaci, setPodaci] = useState({
    ime: '', prezime: '', email: '', lozinka: ''
  })
  const [greska, setGreska] = useState('')
  const [greskePolja, setGreskePolja] = useState({})
  const [ucitava, setUcitava] = useState(false)

  const { registrujSe } = useAuth()
  const navigate = useNavigate()

  const promeni = (e) => {
    setPodaci({ ...podaci, [e.target.name]: e.target.value })
  }

  const posalji = async (e) => {
    e.preventDefault()
    setGreska('')
    setGreskePolja({})
    setUcitava(true)

    try {
      await registrujSe(podaci)
      navigate('/termini')
    } catch (err) {
      const odgovor = err.response?.data
      setGreska(odgovor?.poruka || 'Greška pri registraciji')
      setGreskePolja(odgovor?.greskePolja || {})
    } finally {
      setUcitava(false)
    }
  }

  const polje = (naziv, labela, tip = 'text') => (
    <div className="mb-3">
      <label className="form-label">{labela}</label>
      <input
        type={tip}
        name={naziv}
        className={`form-control ${greskePolja[naziv] ? 'is-invalid' : ''}`}
        value={podaci[naziv]}
        onChange={promeni}
        required
      />
      {greskePolja[naziv] && (
        <div className="invalid-feedback">{greskePolja[naziv]}</div>
      )}
    </div>
  )

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="card">
            <div className="card-body">
              <h4 className="card-title mb-4">Registracija</h4>

              {greska && <div className="alert alert-danger">{greska}</div>}

              <form onSubmit={posalji}>
                {polje('ime', 'Ime')}
                {polje('prezime', 'Prezime')}
                {polje('email', 'Email', 'email')}
                {polje('lozinka', 'Lozinka', 'password')}

                <button type="submit" className="btn btn-primary w-100" disabled={ucitava}>
                  {ucitava ? 'Registracija...' : 'Registruj se'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
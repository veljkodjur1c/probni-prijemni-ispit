import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Prijava() {
  const [email, setEmail] = useState('')
  const [lozinka, setLozinka] = useState('')
  const [greska, setGreska] = useState('')
  const [ucitava, setUcitava] = useState(false)

  const { prijaviSe } = useAuth()
  const navigate = useNavigate()

  const posalji = async (e) => {
    e.preventDefault()
    setGreska('')
    setUcitava(true)

    try {
      await prijaviSe(email, lozinka)
      navigate('/termini')
    } catch (err) {
      setGreska(err.response?.data?.poruka || 'Greška pri prijavljivanju')
    } finally {
      setUcitava(false)
    }
  }

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="card">
            <div className="card-body">
              <h4 className="card-title mb-4">Prijava na sistem</h4>

              {greska && <div className="alert alert-danger">{greska}</div>}

              <form onSubmit={posalji}>
                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Lozinka</label>
                  <input
                    type="password"
                    className="form-control"
                    value={lozinka}
                    onChange={(e) => setLozinka(e.target.value)}
                    required
                  />
                </div>

                <button type="submit" className="btn btn-primary w-100" disabled={ucitava}>
                  {ucitava ? 'Prijavljivanje...' : 'Prijavi se'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
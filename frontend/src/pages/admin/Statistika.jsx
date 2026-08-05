import { useEffect, useState } from 'react'
import {
  BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts'
import { statistikaApi } from '../../api/servisi'

const BOJE = ['#ffc107', '#198754', '#6c757d']

export default function Statistika() {
  const [podaci, setPodaci] = useState(null)
  const [greska, setGreska] = useState('')
  const [ucitava, setUcitava] = useState(true)

  useEffect(() => {
    statistikaApi.ucitaj()
      .then(o => setPodaci(o.data))
      .catch(() => setGreska('Greška pri učitavanju statistike'))
      .finally(() => setUcitava(false))
  }, [])

  if (ucitava) return <div className="container">Učitavanje...</div>
  if (greska) return <div className="container"><div className="alert alert-danger">{greska}</div></div>

  const poStatusu = [
    { naziv: 'Na čekanju', broj: podaci.naCekanju },
    { naziv: 'Prijavljen', broj: podaci.prijavljenih },
    { naziv: 'Otkazana', broj: podaci.otkazanih }
  ].filter(x => x.broj > 0)

  const kartica = (naslov, vrednost, klasa = '') => (
    <div className="col-md-3 mb-3">
      <div className="card h-100">
        <div className="card-body">
          <p className="text-muted small mb-1">{naslov}</p>
          <p className={`fs-4 mb-0 ${klasa}`}>{vrednost}</p>
        </div>
      </div>
    </div>
  )

  return (
    <div className="container">
      <h3 className="mb-4">Statistika</h3>

      <div className="row">
        {kartica('Ukupno prijava', podaci.ukupnoPrijava)}
        {kartica('Naplaćeno', `${podaci.ukupanPrihod} RSD`, 'text-success')}
        {kartica('Očekuje se', `${podaci.ocekivanPrihod} RSD`, 'text-warning')}
        {kartica('Prijavljenih', podaci.prijavljenih)}
      </div>

      <div className="row mt-3">
        <div className="col-lg-5 mb-4">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title mb-4">Prijave po statusu</h5>
              {poStatusu.length === 0 ? (
                <p className="text-muted">Nema podataka.</p>
              ) : (
                <ResponsiveContainer width="100%" height={280}>
                  <PieChart>
                    <Pie
                      data={poStatusu}
                      dataKey="broj"
                      nameKey="naziv"
                      outerRadius={95}
                      label
                    >
                      {poStatusu.map((_, i) => (
                        <Cell key={i} fill={BOJE[i % BOJE.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>
        </div>

        <div className="col-lg-7 mb-4">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title mb-4">Broj prijava po terminu</h5>
              {podaci.poTerminima.length === 0 ? (
                <p className="text-muted">Nema podataka.</p>
              ) : (
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={podaci.poTerminima}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="naziv" fontSize={12} />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="broj" name="Prijava" fill="#0d6efd" />
                  </BarChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-12">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title mb-4">Prihod po vrsti ispita</h5>
              {podaci.poVrstiIspita.length === 0 ? (
                <p className="text-muted">Nema podataka.</p>
              ) : (
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={podaci.poVrstiIspita}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="naziv" />
                    <YAxis />
                    <Tooltip formatter={(v) => `${v} RSD`} />
                    <Legend />
                    <Bar dataKey="iznos" name="Iznos (RSD)" fill="#198754" />
                  </BarChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navigacija from './components/Navigacija'
import ZasticenaRuta from './components/ZasticenaRuta'
import Prijava from './pages/Prijava'
import Registracija from './pages/Registracija'
import Termini from './pages/Termini'
import MojePrijave from './pages/MojePrijave'
import AdminTermini from './pages/admin/AdminTermini'
import AdminPrijave from './pages/admin/AdminPrijave'
import SpisakPoTerminu from './pages/admin/SpisakPoTerminu'
import AdminKatalog from './pages/admin/AdminKatalog'
import Statistika from './pages/admin/Statistika'
import Pocetna from './pages/Pocetna'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navigacija />
        <Routes>
          <Route path="/" element={<Pocetna />} />
          <Route path="/" element={<Navigate to="/termini" replace />} />
          <Route path="/prijava" element={<Prijava />} />
          <Route path="/registracija" element={<Registracija />} />
          <Route path="/termini" element={<Termini />} />
          <Route
            path="/moje-prijave"
            element={
              <ZasticenaRuta>
                <MojePrijave />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/termini"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminTermini />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/prijave"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminPrijave />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/spisak"
            element={
              <ZasticenaRuta samoAdmin>
                <SpisakPoTerminu />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/katalozi"
            element={
              <ZasticenaRuta samoAdmin>
                <AdminKatalog />
              </ZasticenaRuta>
            }
          />
          <Route
            path="/admin/statistika"
            element={
              <ZasticenaRuta samoAdmin>
                <Statistika />
              </ZasticenaRuta>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
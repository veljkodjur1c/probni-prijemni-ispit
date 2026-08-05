import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navigacija from './components/Navigacija'
import ZasticenaRuta from './components/ZasticenaRuta'
import Prijava from './pages/Prijava'
import Registracija from './pages/Registracija'
import Termini from './pages/Termini'
import MojePrijave from './pages/MojePrijave'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navigacija />
        <Routes>
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
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
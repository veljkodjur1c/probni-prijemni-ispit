import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ZasticenaRuta({ children, samoAdmin = false }) {
    const { korisnik, jeAdmin } = useAuth()

    if (!korisnik) {
        return <Navigate to="/prijava" replace />
    }

    if (samoAdmin && !jeAdmin) {
    return <Navigate to="/" replace />
    }

  return children
} 
import { createContext, useContext, useState } from 'react'
import { authApi } from '../api/servisi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [korisnik, setKorisnik] = useState(() => {
    const sacuvan = localStorage.getItem('korisnik')
    return sacuvan ? JSON.parse(sacuvan) : null
  })

  const sacuvaj = (odgovor) => {
    const { token, korisnik } = odgovor.data
    localStorage.setItem('token', token)
    localStorage.setItem('korisnik', JSON.stringify(korisnik))
    setKorisnik(korisnik)
  }

  const prijaviSe = async (email, lozinka) => {
    const odgovor = await authApi.prijava({ email, lozinka })
    sacuvaj(odgovor)
  }

  const registrujSe = async (podaci) => {
    const odgovor = await authApi.registracija(podaci)
    sacuvaj(odgovor)
  }

  const odjaviSe = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('korisnik')
    setKorisnik(null)
  }

  const jeAdmin = korisnik?.uloga === 'ADMIN'

  return (
    <AuthContext.Provider value={{ korisnik, jeAdmin, prijaviSe, registrujSe, odjaviSe }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
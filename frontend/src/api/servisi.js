import klijent from './klijent'

export const authApi = {
  registracija: (podaci) => klijent.post('/auth/registracija', podaci),
  prijava: (podaci) => klijent.post('/auth/prijava', podaci)
}

export const terminiApi = {
  svi: () => klijent.get('/termini'),
  buduci: () => klijent.get('/termini?samoBuduci=true'),
  jedan: (id) => klijent.get(`/termini/${id}`),
  kreiraj: (podaci) => klijent.post('/termini', podaci),
  izmeni: (id, podaci) => klijent.put(`/termini/${id}`, podaci),
  obrisi: (id) => klijent.delete(`/termini/${id}`)
}

export const prijaveApi = {
  kreiraj: (terminIds) => klijent.post('/prijave', { terminIds }),
  moje: () => klijent.get('/prijave/moje'),
  sve: (status) => klijent.get('/prijave', { params: { status } }),
  otkaziMoju: (id) => klijent.delete(`/prijave/${id}`),
  otkaziKaoAdmin: (id) => klijent.delete(`/prijave/${id}/admin`)
}

export const uplateApi = {
  evidentiraj: (prijavaId) => klijent.post(`/uplate/prijava/${prijavaId}`),
  zaPrijavu: (prijavaId) => klijent.get(`/uplate/prijava/${prijavaId}`)
}

export const cenovnikApi = {
  sve: () => klijent.get('/cenovnik'),
  postavi: (podaci) => klijent.post('/cenovnik', podaci)
}
export function sacuvajFajl(podaci, naziv) {
  const url = window.URL.createObjectURL(new Blob([podaci]))
  const link = document.createElement('a')
  link.href = url
  link.download = naziv
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}
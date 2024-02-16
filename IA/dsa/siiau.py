import requests
from bs4 import BeautifulSoup
import json

URL = "http://consulta.siiau.udg.mx/wco/sspseca.consulta_oferta?ciclop=202410&cup=D&majrp=&crsep=&materiap=&horaip=&horafp=&edifp=&aulap=&ordenp=0&mostrarp=20000"

request = requests.get(URL, timeout=5)

soup = BeautifulSoup(request.content, 'html.parser')

table = soup.select("table")[0]
filas = table.select("tr")
filas = filas[2:]

results = soup.findAll("tr", {"style" : "background-color:#e5e5e5;"})
results2 = soup.findAll("tr", {"style" : "background-color:#FFFFFF;"})

print(len(results2))
# 1 2 4
results =  results + results2
registros = []
for fila in results:
    columnas = fila.select("td")
    registros.append({
        "Clave":columnas[1].text,
        "Nombre":columnas[2].text,
        "Creditos":columnas[4].text,
        "Horas": "4"
    })

print(len(registros))


ClavesUnicas = []
registrosU = []
for i in registros:
    if i["Clave"] not in ClavesUnicas:
        registrosU.append(i)
        ClavesUnicas.append(i["Clave"])


with open('data.json', 'w') as f:
    json.dump(registrosU, f)

print(len(registrosU))







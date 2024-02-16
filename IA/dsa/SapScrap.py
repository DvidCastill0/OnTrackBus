import requests
from bs4 import BeautifulSoup
import json
import csv

url = 'https://www.se80.co.uk/sap-tables/list/?index='
baseIndex = ['/','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y']


AtibutesKeys = ['Field','Description','Data Element','Data Type','Lenght','Check Table','Conversion Routine','Domain Name','Memory ID','SHLP','Table','Key']
TableKeys = ['header','Name','Description','DeliveryClass','Display/Manteinance','Enhacement']

def getURLIndex(url,index):
    return f'{url}{index}'

def getIndex(url):
    urls = []
    print(url)
    try:
        request = requests.get(url, timeout=5)
    except KeyboardInterrupt:
        exit()

    except:
        print(f"Error en URL={url}")
        file = open("Errors.txt", "a")
        file.write(url)
        file.close()
        return
    soup = BeautifulSoup(request.content, 'html.parser')
    content = soup.find("div", {"class" : "pageContent"})
    indices = content.find_all("h2")
    if len(indices) == 0:
        pTags = content.find_all("p")
        for p in pTags:
            getTable(p.a['href'], p.a.text)
        return 
    for indice in indices:
        getIndex(indice.a['href'])
    return 
    
def getTable(url, Name):
    try:
        request = requests.get(url, timeout=5)
    except KeyboardInterrupt:
        exit()
    except:
        print(f"Error en URL={url}")
        file = open("Errors.txt", "a")
        file.write(url+"\n")
        file.close()
        return
    
    soup = BeautifulSoup(request.content, 'html.parser')
    Data = []
    try:
        
        Data.extend(getRows(Name, True, soup))
        Data.extend(getRows(Name, False, soup))
        if len(Data) > 0:
            WriteDataInCSV(Data, f"Atributes.csv")
        WriteDataInCSV([getTableInfo(soup, Name)], "Tables.csv")
    except:
        print("ERROR FATAL url={url}")
        file = open("Errors.txt", "a")
        file.write(url+"\n")
        file.close()
        raise

def getTableInfo(soup, Name):
    Data = {}
    Data['header'] = Name # id
    Contenido = soup.select("div.pageContent")[0]
    Bs = Contenido.findChildren("b" , recursive=False)
    Data['Name'] = Contenido.h2.text
    Data['Description'] = Contenido.p.text
    sibling = Bs[0].next_sibling.replace('\n', '')
    Data['DeliveryClass'] =  sibling if sibling != "None" else "null"
    sibling = Bs[1].next_sibling.replace('\n', '')
    Data['Display/Manteinance'] =  sibling if sibling != "None" else "null" 
    Data['Enhacement'] = Contenido.a.text
    return Data
    


def WriteDataInCSV(Data, FileName, init = False): 
    file = open(FileName, "a")
    writter =  csv.DictWriter(file, Data[0].keys())
    if init:
        writter.writeheader()
    writter.writerows(Data)
    file.close()


def getRows(Name, Key, soup):
    selector = "key" if Key else "other"
    Data = []
    for Row in soup.select(f"tr.{selector}Field"):
        Cell = Row.find_all("td")
        row = {}
        for index in range(10):
            row[AtibutesKeys[index]] = Cell[index].text
        row['Table'] = Name
        row['Key'] = Key
        Data.append(row)
    return Data

def getSAP(initFiles):
    if initFiles:
        file = open("Tables.csv", "w")
        writter =  csv.DictWriter(file, TableKeys)
        writter.writeheader()
        file.close()
        file = open("Atributes.csv", "w")
        writter =  csv.DictWriter(file, AtibutesKeys)
        writter.writeheader()
        file.close()

    paginas = []
    for index in baseIndex:
        paginas.append(getURLIndex(url, index))
    
    for pagina in paginas:
        getIndex(pagina)
    
    return 

getSAP(True)

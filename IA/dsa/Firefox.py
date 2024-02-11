from selenium import webdriver
from webdriver_manager.firefox import GeckoDriverManager
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.firefox.options import Options
from selenium import webdriver
import random
import csv
from time import sleep

options = Options()
options.headless = False
profile = webdriver.FirefoxProfile()
profile.set_preference("media.autoplay.default", 0)
driver = webdriver.Firefox()
url = "https://moovitapp.com/guadalajara-2900/lines/t13-b%20-%20azucena/89384564/6364548/en?t=1"

Horas = []

def getData():
    driver.get(url)
    sleep(10)
    elements = driver.find_elements(By.XPATH, "//div[@role='listitem']")
    hora : str
    #//div[@class='current ng-star-inserted']
    count = 0
    parada = 1
    for element in elements:
        subCount = 0
        element.click()
        hora = "" 
        while hora == "":
            hora = driver.find_element(By.XPATH, "//div[@class='current ng-star-inserted']").text
            print(parada, hora)
            sleep(.5)
        num = int(hora.split(" ")[0][-1])
        while num != (count % 10):
            count += 1
            subCount += 1  
        
        Horas.append(
            {    
            "Parada":parada,
            "N":count,
            "NF":subCount,
            "hora": hora
            })
        parada += 1
    WriteDataInCSV(Horas, "178.csv")
    

def WriteDataInCSV(Data, FileName, init = False): 
    file = open(FileName, "a")
    writter =  csv.DictWriter(file, Data[0].keys())
    if init:
        writter.writeheader()
    writter.writerows(Data)
    file.close()

try:
    getData()
except:
    print("Error")
finally:
    driver.quit()
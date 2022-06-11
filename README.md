[![Build Status](https://app.travis-ci.com/lucas-gio/mutantes-y-humanos.svg?branch=master)](https://app.travis-ci.com/lucas-gio/mutantes-y-humanos)
[![CodeFactor](https://www.codefactor.io/repository/github/lucas-gio/mutantes-y-humanos/badge)](https://www.codefactor.io/repository/github/lucas-gio/mutantes-y-humanos)
# Mutantes y Humanos-Lucas-Gioia
Magneto quiere reclutar la mayor cantidad de mutantes para poder luchar contra los X-Men.
Sistema de verificación mutantes realizado en java 8 para ingreso de mercadolibre.

#### Se encuentra disponible una versión con tecnología actualizada de este proyecto en: (https://github.com/lucas-gio/mutantes-y-humanos-kotlin)


## Ejecución
Para ejecutar el programa se debe lanzar el main desde la clase com.application.Application.
El archivo application.properties mantiene las configuraciones relacionadas a la base de datos. El puerto por defecto es el 5000.
Para compilar el sistema, se debe situarse en la carpeta del proyecto, y luego ejecutar mvn clean, y luego mvn install. El jar resultante, que se encuentra en /target/MercadoLibreLucasGioia-1.0-SNAPSHOT-shaded.jar contiene tanto la aplicación, como los archivos de recurso, y las dependencias.

## Envío de adn

Para realizar el envío de adn debe generarse una petición POST enviando en el cuerpo del mensaje un valor como el especificado debajo. El mismo se debe realizar a la dirección http://mllucasgioia2-env.eba-ecxkzm5a.sa-east-1.elasticbeanstalk.com/mutant/  .
```sh
{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}
```
  
Una vez finalizado el procesamiento, el sistema responderá con uno de los siguientes estados: 
| Estado | Motivo |
| ------ | ------ |
| 200 | Fué detectado un mutante|
| 400 | Fué detectado un error en el formato json|
| 403 | Fué detectado un humano|
| 422 | Fué detectado un ingreso inválido|
| 500 | Fué detectado un error genérico|

Se considera inválido un ingreso con dna null, sin elementos, o compuesto con dígitos que no son los especificados en el requerimiento (A,T,C,G).

Ante cada ingreso válido, previo al momento de generar la respuesta, se almacena un registro en una base de datos mongodb, con los datos obtenidos. El siguiente es un ejemplo de un registro almacenado:

```sh
{"_id":"5efbe231e22baa065f61806b","dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"],"isMutant":false}
  ```
  
## Estadísticas

Para obtener las estadísticas de humanos y mutantes debe realizarse una petición GET a la dirección http://mllucasgioia2-env.eba-ecxkzm5a.sa-east-1.elasticbeanstalk.com/stats/
Se obtiene como resultado un json que sigue el formato propuesto:
  ```sh
  {"count_mutant_dna":4,"count_human_dna":10,"ratio":0.40}
   ```  
## Rest

  Para los servicios rest se utilizó spark framework configurado por medio del controlador ApiRestController.
  El sistema se empaqueta como jar junto con todas sus dependencias.
  
## Servidores

 Esta aplicación está subida a amazon beanstalk, residente en Brasil. Está compuesto por 10 instancias detrás de un balanceador de carga de tipos t2 medium y t3 medium.
 La base de datos reside en la nube de mongodb, mongodb atlas 4.2.8. Es un grupo de tres host de tipo M30 residente en San Pablo, Brasil.
 
  ## Pruebas de carga
  
  Las pruebas de carga fueron realizadas con loader.io. Sin costo permite hacer pruebas de hasta 10.000 clientes por 1 min. Todas estas pruebas fueron considerando que en 1 minuto se tenga una carga constante de n clientes.
  Estas pruebas tienen un límite de carga por debajo de 1.000.000 de clientes. Para que el sistema soporte tanta cantidad de usuarios se debe invertir en más instancias del lado de amazon, y planes de cluster más costosos del lado de mongodb atlas.
  
 ### Prueba enviando mutantes
  Se realiza un post a mutantes con la info siguiente:
```sh
  {"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}
```
  Se presentan errores a partir de los 3000 clientes concurrentes. Leyendo los logs aparecen los siguientes mensajes:
  * Nginx - 2020/07/05 03:51:53 [alert] 8702#0: 1024 worker_connections are not enough
  * Log de acceso: 172.31.7.253 - - [05/Jul/2020:03:52:40 +0000] "POST /mutant/ HTTP/1.1" 499 0 "-" "loader.io;92972a888c41536f5b7e62afb6d62509" "54.84.218.101"
  
  ### 100 clientes en 1 min
  ![](https://i.imgur.com/fN6ePlN.png)
  ![](https://i.imgur.com/MJFfCDr.png)
  
  ### 250 clientes en 1 min
  ![](https://i.imgur.com/9NhT5jv.png)
  ![](https://i.imgur.com/0jShm33.png)
  
  ### 500 clientes en 1 min
  ![](https://i.imgur.com/rNvnwiw.png)
  ![](https://i.imgur.com/cLSGrrr.png)
  
  ### 1000 clientes en 1 min
  ![](https://i.imgur.com/shD7lDl.png)
  ![](https://i.imgur.com/w0MUdCH.png)
  
  ### 2500 clientes en 1 min
  ![](https://i.imgur.com/8BhUZyj.png)
  ![](https://i.imgur.com/ku6etsm.png)
    
  ### 3000 clientes en 1 min
  ![](https://i.imgur.com/tNHSgQO.png)
  ![](https://i.imgur.com/mpKfh3M.png)
  
  ### 5000 clientes en 1 min
  ![](https://i.imgur.com/BXEaKs8.png)
  ![](https://i.imgur.com/P71USUr.png)
    
  ### Prueba enviando humanos
  Esta prueba no se puede realizar con loader.io. Toma a las respuestas de estado 4xx como erróneas y no continúa con su ejecución después de varias respuestas.
  
  ### Prueba recibiendo estadísticas
  Luego de enviar los mutantes anteriores, se realizó la prueba de rendimiento sobre estadísticas.
  Se presentan errores a partir de los 5500 clientes concurrentes en este caso. 
  Leyendo los logs aparecen los siguientes mensajes:
  * Nginx: 2020/07/05 03:51:53 [alert] 8702#0: 1024 worker_connections are not enough
  * Log de acceso: 172.31.43.235 - [05/Jul/2020:04:22:58 +0000] "GET /stats/ HTTP/1.1" 499 0 "-" "loader.io;92972a888c41536f5b7e62afb6d62509" "18.213.246.177"
  
 ### 100 clientes en 1 min
  ![]( https://i.imgur.com/c9u9D8J.png)
  ![](https://i.imgur.com/RVbIzHx.png)

 ### 250 clientes en 1 min
 ![](https://i.imgur.com/NheBeXQ.png)
 ![](https://i.imgur.com/WGFZvms.png)
 
 ### 500 clientes en 1 min
 ![](https://i.imgur.com/LeCsgpy.png)
 ![](https://i.imgur.com/Y30aVJL.png)
 
 ### 1000 clientes en 1 min
 ![](https://i.imgur.com/HmLSnX2.png)
 ![](https://i.imgur.com/4cc1rxb.png)
 
 ### 2500 clientes en 1 min
 ![](https://i.imgur.com/n6HnKdV.png)
 ![](https://i.imgur.com/wycfd8c.png)
 
 ### 5000 clientes en 1 min
 ![](https://i.imgur.com/lUlZ5Z5.png)
 ![](https://i.imgur.com/9G09mGl.png)
 
 ### 5500 clientes en 1 min
 ![](https://i.imgur.com/SUufrNl.png)
 ![](https://i.imgur.com/73cSsjS.png)
 
 ### 10.000 clientes en 1 min
 ![](https://i.imgur.com/FhvR0EW.png)
 ![](https://i.imgur.com/IaZPbxG.png)
 
 ### Estado de amazon luego de las pruebas realizadas
 ![](https://i.imgur.com/BiKyiuL.png)
 
 
  ## Pruebas automáticas y cobertura
  
 Las pruebas automáticas, tanto unitarias como de integración, están realizadas con el framework spock y el lenguaje groovy (requerido por spock).
 Ejecutando los test con cobertura, intellij genera una tabla informativa
 
Cobertura general
|Package|Class, % |	Method, % |	Line, %|
| ------ | ------ | ------ | ------ |
| all classes |	91,7% (11/ 12)  |	92,7% (51/ 55)  |	84,8% (229/ 270)  | 

Cobertura por paquetes
|Package| 	Class, % |	Method, % |	Line, %|
| ------ | ------ | ------ | ------ |
|com.application.utils |	0% (0/ 1) |	0% (0/ 1) |	0% (0/ 1)|
|com.application.services.stats| 	100% (1/ 1) |	100% (2/ 2) |	84,2% (16/ 19) |
|com.application.domain |	100% (3/ 3)  |	94,7% (18/ 19)  |	97,8% (44/ 45) |
|com.application.exceptions |	100% (2/ 2) |	100% (2/ 2) |	100% (4/ 4)|
|com.application.services.mutant |	100% (1/ 1) |	100% (9/ 9) |	81,5% (53/ 65)|
|com.application.services.mongo |	100% (1/ 1) |	75% (3/ 4)| 	 	81,1% (30/ 37) |
|com.application.controllers.api |	100% (1/ 1) |	87,5% (7/ 8)  |	81,2% (39/ 48)  |
|com.application.services.api |	100% (1/ 1) 	|100% (7/ 7)  	|83,3% (30/ 36)  |
|com.application |	100% (1/ 1) |	100% (3/ 3) |	86,7% (13/ 15) |

## Notas
* La base de datos de pruebas es la misma que la que se usa para cargar mutantes. En un entorno real deberían ser distintas. Está así por un tema de costos.
* La inyección de dependencias tal vez no sea como se maneja en spring en una aplicación java clásica. Investigué esa parte ya que en grails es más simplificado el manejo de dependencias. 
* En pruebas automáticas con grails, las inyecciones de dependencias funcionan, pero no con un sistema java. Por eso aparecen new de servicios.
* El archivo properties queda empaquetado en el jar, debería estar fuera así como también los jar de las dependencias. Esto es por una limitante de amazon beanstalk donde cargué el código ya que permitía sólamente el ingreso de un archivo.

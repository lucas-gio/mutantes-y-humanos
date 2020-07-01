# MercadoLibre-Lucas-Gioia

Sistema de verificación mutantes realizado en java 8.

# Ejecución
Para ejecutar el programa se debe lanzar el main desde la clase com.application.Application.

# Envío de adn

Para realizar el envío de adn debe generarse una petición POST enviando en el cuerpo del mensaje un valor como el especificado debajo. El mismo se debe realizar a la dirección <completar>.
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
  
# Estadísticas

Para obtener las estadísticas de humanos y mutantes debe realizarse una petición GET a la dirección <completar>/stats/
Se obtiene como resultado un json que sigue el formato propuesto:
  ```sh
  {"count_mutant_dna":4,"count_human_dna":10,"ratio":0.40}
   ```
  
# Pruebas automáticas

 Las pruebas automáticas, tanto unitarias como de integración, están realizadas con el framework spock y el lenguaje groovy (requerido por spock).
  
# WEB

  Para los servicios rest se utilizó spark framework configurado por medio del controlador ApiRestController.
  
# Servidores

 Esta aplicación está subida a amazon beanstalk, residente en Brasil. Se empaqueta como jar junto con todas sus dependencias.
 La base de datos reside en la nube de mongodb, mongodb atlas 4.2.8. Es un grupo de tres host residente en San Pablo, Brasil.
 
 # Cobertura
 
 Ejecutando los test con cobertura, intellij genera una tabla informativa
 
Cobertura general
|Package|Class, % |	Method, % |	Line, %|
| ------ | ------ | ------ | ------ |
| all classes |	90,9% (10/ 11) |	96,1% (49/ 51) |	85,6% (208/ 243) | 

Cobertura por paquetes
|Package| 	Class, % |	Method, % |	Line, %|
| ------ | ------ | ------ | ------ |
|com.application.utils |	0% (0/ 1) |	0% (0/ 1) |	0% (0/ 1)|
|com.application.services.stats| 	100% (1/ 1) |	100% (2/ 2) |	81,2% (13/ 16)|
|com.application.domain |	100% (2/ 2) |	100% (17/ 17) |	100% (39/ 39)|
|com.application.exceptions |	100% (2/ 2) |	100% (2/ 2) |	100% (4/ 4)|
|com.application.services.mutant |	100% (1/ 1) |	100% (9/ 9) |	81,5% (53/ 65)|
|com.application.services.mongo |	100% (1/ 1) |	75% (3/ 4)| 	78,8% (26/ 33)|
|com.application.controllers.api |	100% (1/ 1) |	100% (7/ 7) |	83,7% (36/ 43) |
|com.application.services.api |	100% (1/ 1) 	|100% (6/ 6) 	|88,9% (24/ 27) |
|com.application |	100% (1/ 1) |	100% (3/ 3) |	86,7% (13/ 15) |

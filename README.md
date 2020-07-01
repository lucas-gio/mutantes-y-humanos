# MercadoLibre-Lucas-Gioia

Sistema de verificación mutantes realizado en java 8.

# Envío de adn

Para realizar el envío de adn debe generarse una petición POST enviando en el cuerpo del mensaje un valor como el especificado debajo. El mismo se debe realizar a la dirección <completar>.
```sh
{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}
```
  
Una vez finalizado el procesamiento, el sistema responderá con un estado 200, si fué detectado un mutante; 403, si el ingreso fué de un humano; 422, si se detectó un ingreso inválido; o bien 500 en caso de error genérico.

Se considera inválido un ingreso con dna null, sin elementos, o compuesto con dígitos que no son los especificados en el requerimiento (A,T,C,G).

Ante cada ingreso válido, previo al momento de generar la respuesta, se almacena un registro en una base de datos mongodb, con los datos obtenidos. El siguiente es un ejemplo de un registro almacenado:
<completar>
  
# Estadísticas

Para obtener las estadísticas de humanos y mutantes debe realizarse una petición GET a la dirección <completar>/stats/
Se obtiene como resultado un json que sigue el formato propuesto:
  <completar con rta.>
  
# Pruebas automáticas

 Las pruebas automáticas, tanto unitarias como de integración, están realizadas con el framework spock y el lenguaje groovy (requerido por spock).
 
 
  
# WEB

  Para los servicios rest se utilizó spark framework configurado por medio del controlador ApiRestController.
  
# Servidores

 Esta aplicación está subida a amazon beanstalk, residente en Brasil. Se empaqueta como jar junto con todas sus dependencias.
 La base de datos reside en la nube de mongodb, mongodb atlas 4.2.8. Es un grupo de tres host residente en San Pablo, Brasil.

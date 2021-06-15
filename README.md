
# Servicio Backend de la aplicación Meteovereda

  

En esta guía se van a describir los pasos para desplegar correctamente el backend de la aplicación .

Para el software que se haya instalado y configurado según su guía oficial, se dejará el link a la guía externa para no sobrecargar esta.

  
  
  

# Entorno de funcionamiento

Se recomienda tener un servidor con **ubuntu server**(última version LTS) con al menos **1vcore, 1gb ram y 5gb de almacenamiento** libre después de la instalación.

Además si se desea usar Docker, y el servidor es de tipo VPS, se debe asegurar que se usa la virutalización **KVM** y no OpenVz, puesto que esta última no compatible nativamente.

  
  

## Preparación previa

  

El servicio funciona con la version 11 de Java. Debemos instalar el JRE si vamos a subir la aplicación como JAR, y el JDK si no vamos a crear el jar, y vamos a ejectuar, probarla o crear el JAR.

En mi caso usé openjdk.

  

JRE:

``sudo apt-get install openjdk-11-jre``

JDK:

``sudo apt-get install openjdk-11-jdk``

  
  

Instalaremos UFW para gestionar los puertos:

``sudo apt-get install ufw``

Y abriremos el puerto SSH si lo usamos.

  

Además si pretendemos usar Docker, lo instalaremos junto a docker-compose ([Guía oficial](https://docs.docker.com/engine/install/))

  

## Base de datos

La aplicación funciona con la mayoría de BBDD SQL, ya sea MySql, MariaDB, PostgreSQL.

En mi caso usé la distribución de PostgreSQL de Percona debido a que es OpenSource y cuenta con funcionalidades extras que se pueden configurar (estadísticas, copias de seguridad, redundancia).

[Installing Percona Distribution for PostgreSQL](https://www.percona.com/doc/postgresql/LATEST/installing.html#installing-percona-distribution-for-postgresql)

Se debe de tener cuidado instalando las extensiones, pues si se deja alguna sin configurar(de las que indica) no funciona correctamente.

>Una vez instalada la base de datos PostgreSQL, hay que crear el schema **weather**

Si solo se quiere una BBDD sin ninguna funcionalidad añadida, es más recomendable instalar PostgreSQL normal o cualquier otro SQL (MySQL o MariaDB por ejemplo)

  

También podemos instalar una BBDD vía docker.

Dejo **ejemplo** del archivo docker-compose para configurar una BBDD PostgreSQL:
```
version: '3'
services:
  postgresql:
    build: .
    restart: always
    volumes:
      - ./postgres-data:/var/lib/postgresql/data
    ports:
      - 5432:5432
    environment:
      - POSTGRES_DB=meteo
      - POSTGRES_USER=meteo
      - POSTGRES_PASSWORD=PassW0rD
volumes:
  postgres-data:
    external: false

```
  

archivo Dockerfile.
```
FROM postgres:13

COPY init.sql /docker-entrypoint-initdb.d/
```
Y lo levantamos con:
``sudo docker-compose up``
  

## Configuración adicional de Base de Datos.

En caso de que queramos acceder de forma externa a la base de datos, debemos abrir el puerto de la base de datos, y editar la configuración de esta para permitir accesos externos(si hace falta).

Además en caso de no usar una base de datos PostgreSQL, deberemos modificar el código del servicio y borrar toda referencia a los **schemas**.


## Configuración servicio Spring Boot
En **application.properties** editaremos las siguientes variables:
```
-Puerto
-Database variables
-Email variables
-OpenWeather token
-Telegram variables
-Mail to recieve error alerts
```
Si usamos otro base de datos que no sea PostgreSQL, tendremos que cambiar la dependencia de la base de datos en pom.xml y eliminar toda referencia a **schema**.

Para editar la frecuencia con la que se guardan los datos, editaremos el string cron en **WeathersCron.java**

## Despliegue servicio Spring Boot
Para desplegar el servicio, lo descargamos y editamos la configuación necesaria.
Si se usa postgreSQL, lo mas facil es descargar la carpeta despliegue del repositorio, y configurar el application.properties, subirlo al servidor y desplegar con el siguiente comando:
```
java -jar [RUTA ABSOLUTA A LA CARPETA]/station-0.0.1-SNAPSHOT.jar --spring.config.location=file:[RUTA ABSOLUTA A LA CARPETA]/application.properties
```

En caso de querer iniciar sin crear el package, se puede con (estando ubicados en la carpeta):
``./mvnw spring-boot:start``
o
``./mvnw spring-boot:run``


Para crear el package de nuevo:
``./mvnw clean package -Dmaven.test.failure.ignore=true``
y se creará el JAR en la carpeta target



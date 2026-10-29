# Payment MQ Router

Application qui lit les messages d'une file **IBM MQ**, les enregistre dans une base **Oracle** et les affiche via une **API REST** et une interface **Angular**.


IBM MQ  →  Spring Boot  →  Oracle  →  API REST  →  Angular


## Technologies

Java 21, Spring Boot 4, IBM MQ, Oracle, Angular 22, Maven, Docker.

## Prérequis

- Docker Desktop
- Git

## Lancer l'application


git clone https://github.com/bechirjlidi1919/mq-message-viewer.git
cd mq-message-viewer
docker compose up -d --build


 Vérifier que les 4 conteneurs tournent :


docker compose ps


## Accès

- **Interface web** : http://localhost:4200
- **API REST** : http://localhost:8081/api/messages
- **Console IBM MQ** : https://localhost:9443/ibmmq/console (`admin` / `password`)
- **Oracle** : `localhost:1521`, service `FREEPDB1` (`paymentTest` / `paymentTest`)

## Tester

*Envoyer 1000 messages dans la file MQ :

docker exec ibmmq bash -c "seq -f Message-de-test-%g 1 1000 | /opt/mqm/samp/bin/amqsput DEV.PAYMENT.IN QM1"

*Les messages apparaissent ensuite dans l'interface web : http://localhost:4200

## API REST

- `GET /api/messages?page=0&size=20&search=texte` : liste paginée, recherche optionnelle
- `GET /api/messages/all` : tous les messages
- `GET /api/messages/{id}` : détail d'un message (404 s'il n'existe pas)

## Tests unitaires


cd backend
./mvnw test "-Dtest=*Test"



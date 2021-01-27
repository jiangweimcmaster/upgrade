### Prerequisites

Docker
Maven
Java11

### Setup

1. run sql server :
```
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Password1" -p 1433:1433 --name sql1 -h sql1 -d mcr.microsoft.com/mssql/server:2019-latest
```
setup db: create db "upgrade"


2. install and run :
```
mvn clean install
mvn spring-boot:run
```
Or using Intellij to build and run

### API contracts

/api/booking/status
To get the avaliable dates
Path parameters: startDate (not required, default tomorrow), endDate (not required, default 1 month later)
Dates need to follow the rules (tomorrow to 1 month later)

/api/booking/reserve
To make the reservation
Body example:
```
{
    "name" : "Jiang",
    "email" : "jiang@jiang.com",
    "startDate" : "2021-02-10",
    "endDate" : "2021-02-11"
}
```
Dates same requirement

/api/booking/change
To modify the reservation
Path: id
Body example as before
Dates same requirement

/api/booking/cancel
To cancel the reservation
Path: id

Check the Postman project for details


### Things to mention
1. The better way to handle error message is to implement custom exception handling
2. May use a new field uid instead of id (auto generate instead of one by one)
3. Can use Jmeter to test concurrent requests
4. Can implement Swagger for better document
5. May consider the security framework
6. May need to use Transictional in repository to deal with high traffic
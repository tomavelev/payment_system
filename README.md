Sample project demonstrating Spring Boot with React front-end. It was not completed fully because of lack of time.
It shows:

* Spring Repository as Data Access
* Spring Services
* Rest Endpoints
* Spring Secutiry
* Cron Job - for deleting old transactions
* Import/Generate of the admins/merchants list - from CSV
* Work in Progress Unit Tests

I've configured the Database - as a Docker (Desktop) image from the start - with how I bootstrapped it here: https://github.com/tomavelev/payment_system/blob/main/db_docker_initialization.txt 

The front-end is ReactJS: 

* Merchant mode: with visibility only on transactions (readonly)
* Admin mode - with visibility on users (with Read/Update/Delete) and transactions (readonly)

I had no prior experience of ReactJS. The code may be a little bit messy - because I've spend more time to make it work than to organize it. I've noticed the similarities with Flutter/and any other platform I've scratched over the years/.
Ideally - The same model/repo/service/controller/view pattern/structure should be used.

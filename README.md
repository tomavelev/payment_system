Sample project demonstrating Spring Boot with React front-end. It was not completed fully because of lack of time.
It shows:

* Spring Repository as Data Access
* Spring Services
* Rest Endpoints
* Spring Secutiry
* Cron Job - for deleting old transactions
* Import/Generate of the admins/merchants list - from CSV - during start-up and from the Admin interface
* Work in Progress Unit Tests


The app could be started up
* as a standalone Spring Boot Application if you have postgres database running
* you could follow the instructions: https://github.com/tomavelev/payment_system/blob/main/db_docker_initialization.txt to start the database as a docker container 
* Or - You could start the database and the app from the docker-compose configuration https://github.com/tomavelev/payment_system/blob/main/payment/docker-compose.yml

To Import the initial DB - when running the app in Docker - the commented section of Dockerfile must be activated: 
https://github.com/tomavelev/payment_system/blob/main/payment/Dockerfile

All secure endpoints require a valid JWT Token - received - after successful authentication. Otherwise - the backend will return HTTP status code: 403 - Forbidden.

To get a valid JWT one must hit the fllowing endpoint:

curl --request POST {host}/public/login -d 'email=user_email&password=yourpassword'
The response is the JSON object:
{
    token(string), //may be null or a valid JWT Token
    role(string), //may be null or values ADMIN, MERCHANT
    code(string) //should not be null and some of the values from here 
    // https://github.com/tomavelev/payment_system/blob/main/payment/src/main/java/com/tomavelev/payment/model/response/BusinessCode.java
}


As both the number of Users and Transactions may go up endlessly (they are not nomenclature) - the get endpoints are paginated.
- Get Uesrs
curl --request GET {host}/admin/users -H "Authorization: Bearer: thetoken}" -d 'offset=0&limit=10'
The response is: 
{
    list: {
        ....
    },
    count: 10, //total cout,
    message: '' , //may be null
    code: 'SUCCESS'
}

- Get Transactions
curl --request GET {host}/transactions -H "Authorization: Bearer: thetoken}" -d 'offset=0&limit=10'
The response is: 
{
    list: {
        {
            'id':'transaction-id', 
            'amount': '0.1',
            ....
        }
        ....
    },
    count: 10, //total cout,
    message: '' , //may be null
    code: 'SUCCESS'
}

- Post Transaction
curl --request GET {host}/transactions -H "Authorization: Bearer: thetoken}" 
-d '{"uuid":"uuid-uuid-uuid","customerEmail":"test@test.test","customerPhone":"+35988888888", "amount":"0.1", "status":"REFUNDED","referenceId":"other-transaction-id"}'

The front-end is ReactJS: 

* Login (public) side of the application.
Authenticated section is secured with a JWT stored (insecurely) in the localStorage.

* Merchant mode: with visibility only on transactions, read/write
* Admin mode: with visibility on users (with Create/Read/Update/Delete), Import from CSV and transactions (read/write)

I had no prior experience of ReactJS. The code may be a little bit messy - because I've spend more time to make it work than to organize it. I've noticed the similarities with Flutter/and any other platform I've scratched over the years/.
Ideally - The same model/repo/service/controller/view pattern/structure should be used.

React.Components are just like Stateful Widget (https://api.flutter.dev/flutter/widgets/StatefulWidget-class.html) - a class that have
* initial state map
* setState method that mutates the state 
* render (equivalent of build(_) in flutter)
* did componentDidUpdate(prevState, prevProp). Similar methods to Flutter - where the rebuild of the parent changes the value parameters passed to the child component
* componentDidMount, componentWillUnmount (hook methods for custom actions when a components are attached/detached)

* Both StatefulWidget and ReactJS Component are UI widgets that may or may not be a representation of full page. 
* All the components that interact with the Back-End have (or should have) a loading flag that disables them - so no double-submit would be possible.  


# About this project 💂
This is just a super simple lightweight project to familiarize myself with Kotlin again,
and learn more about Ktor and authentication.<br>
It focuses on a simple approach of using JWT (JSON Web Token) and OAuth2 principles for
authentication.


## Structure
Keeping it simple, as I'm still learning:
```
src/
├── main/
│   ├── kotlin/
│   │   ├── Application.kt              # Main (Orchestrator) for running (the ktor API server) 
│   │   ├── model/                      # 'Base' Data Classes used and built on by rest of implementation
│   │   │   ├── LoginRequest.kt
│   │   │   ├── TokenResponse.kt
│   │   │   ├── User.kt
│   │   │   └── UserInfo.kt
│   │   ├── plugins/                    # Helping functions
│   │   │   ├── CORS.kt                 # Handling cross-origin requests - whatever that means?
│   │   │   ├── Routing.kt              # Simple Overview/General routing, and calling implemented 'routes/'
│   │   │   ├── Security.kt             # ? - Decoding JWT to verify username in provided JWT? 
│   │   │   └── Serialization.kt        # Tells ktor how to read and handle JSON in the API
│   │   ├── routes                      # Different endpoints to the API
│   │   │   ├── AuthRoutes.kt           # Authentication related: '/auth/login' (POST for login, and GET for helping info)
│   │   │   └── UserInfoRoutes.kt       # User Information: '/userinfo' (user only able to get information about ones account with correct tokens)
│   │   └── service/                    # ?
│   │       ├── JwtService.kt           # Generating Access- and ID Token for the user
│   │       └── UserService.kt          # Creating DB (mutableListOf) of User data class with some helper functions to find the user
│   └── resources/                      # Not really used
│       └── application.conf
```


## How to run 🙋‍♂️
To build and run, either by using IntelliJ IDEA´s Gradle tool on the right side, or by 
running the following commands:
```bash
./gradlew build
```
```bash
./gradlew run
```

### 0.5. Info for Authenticating
To get super simple info for getting started with the tokens, paste ```http://localhost:8080/auth/login``` in a browser.

### 1. Authenticating and getting tokens
To get the Access Token and ID Token, run the following command:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "gandalf_the_grey", "password": "M@g1c"}'
```
> [!NOTE]
> For storing users, I have currently only created super simple "in-memory storage" for simplicity. 

For a successful call, you will then get a similar output as response:
```json
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtaW5pLWlkLXVzZXJzIiwiaXNzIjoibWluaS1pZC1zZXJ2aWNlIiwidXNlcm5hbWUiOiJnYW5kYWxmX3RoZV9ncmV5IiwidXNlcl9pZCI6IjEiLCJzY29wZSI6InJlYWRfd3JpdGUiLCJleHAiOjE3NTA0NDYwNDh9.sNDgSrHxY1uv0nffKEQKRktlXuHHmVn2ROd-nvCoBns",
    "id_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtaW5pLWlkLXVzZXJzIiwiaXNzIjoibWluaS1pZC1zZXJ2aWNlIiwic3ViIjoiMSIsIm5hbWUiOiJHYW5kYWxmIiwiZW1haWwiOiJnYW5kYWxmQGdyZXkuY29tIiwidXNlcm5hbWUiOiJnYW5kYWxmX3RoZV9ncmV5IiwiaWF0IjoxNzUwNDQyNDQ4LCJleHAiOjE3NTA0NDYwNDh9.eZUR5QRaVQU-tyT-oXHRF0I21NrHW3Qp0Nhx790Fh08"
}
```
> [!NOTE]
> These tokens will expire after 1 hour (3600 seconds)


### 2. Using token/-s
Keeping this simple, you can test out if this authentication process works by pasting in the 
`access_token` in this command: `curl -H "Authorization: Bearer <access-token>" http://localhost:8080/userinfo`. Eg:
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtaW5pLWlkLXVzZXJzIiwiaXNzIjoibWluaS1pZC1zZXJ2aWNlIiwidXNlcm5hbWUiOiJnYW5kYWxmX3RoZV9ncmV5IiwidXNlcl9pZCI6IjEiLCJzY29wZSI6InJlYWRfd3JpdGUiLCJleHAiOjE3NTA0NDYwNDh9.sNDgSrHxY1uv0nffKEQKRktlXuHHmVn2ROd-nvCoBns" http://localhost:8080/userinfo
```
Where you will get a similar response:
```json
{
    "sub": "1",
    "name": "Gandalf",
    "email": "gandalf@grey.com",
    "username": "gandalf_the_grey"
}
```
> [!NOTE]
> This works because we "decode" the "user_info" from the token, and matches this with the username
> from stored users.
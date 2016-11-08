Feature: Web interactions
 
Scenario: User creation
  When an HTTP GET request is made on resource /create_user?firstName=Jessica&lastName=Alba
  When an HTTP GET request is made on resource /list_users
  Then the HTTP response body should be ["Jessica Alba"]

Scenario Outline: an incremented ID is assigned to every created user
  When an HTTP GET request is made on resource /create_user?firstName=<firstName>&lastName=<lastName>
  Then the HTTP response code should be OK
  Then the HTTP response body should be User saved : id=<id>

Examples:
    | firstName | lastName  | id |
    | Scarlett  | Johansson | 2  |
    | Angelina  | Jolie     | 3  |

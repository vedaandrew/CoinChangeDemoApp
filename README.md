### Coin change machine ######
Used Spring boot for Rest API implementation,
- applied Dependency Injection design patten in spring that makes code loosely coupled and
easy to manage and write testable code
- Use maven to build module as JAR CoinChangeDemoApp application, CoinChangeDemoApp-0.0.1-SNAPSHOT.jar
- To run the application as jar file: java CoinChangeDemoApp-0.0.1-SNAPSHOT.jar, 
-this will start spring boot application as vending machine started and running.
  (or)
- To run from main class from IDE: CoinChangeDemoApp.java.
- To run Rest url, use postman tool for sending request and response as listed in json format below

- Rest Api:
1. Api to load coins to inventory, this api used to store initial coins float to coin changer machine
- Rest Url: http://localhost:8086/api/v1/coins/loadCoins
- Input request Type : Put mapping, Input@RequestBody: Sample Json Input request 
- {
"coinBoxes": [
{
"coin": "FIVE_PENCE",
"quantity": 8 }, {
"coin": "TEN_PENCE",
"quantity": 8 }, {
"coin": "TWENTY_PENCE",
"quantity": 4 }, {
"coin": "FIFTY_PENCE",
"quantity": 1 }, {
"coin": "HUNDRED_PENCE",
"quantity": 0 }
]
}

2. Api to calculate the coins change and list summary of coins to user for same total amount
- Rest Url: http://localhost:8086/api/v1/coins/getChanges
- Request Type : Post mapping, Input@RequestBody: Sample Json Input request 
- {
"coinBoxes": [
{
"coin": "HUNDRED_PENCE",
"quantity": 0 }, {
"coin": "FIFTY_PENCE",
"quantity": 0 }, {
"coin": "TWENTY_PENCE",
"quantity": 1 }, {
"coin": "TEN_PENCE",
"quantity": 2 }, {
"coin": "FIVE_PENCE",
"quantity": 2 }
]
}

3. Api to accept the summary of coins also update inventory by remove dispensed coins and add user coins in inventory
- Rest Url: http://localhost:8086/api/v1/coins/acceptChanges
- Request Type : Put mapping, Input@RequestBody: Sample Json Input request that accepted by user 
- {
"coinBoxes": [
{
"coin": "HUNDRED_PENCE",
"quantity": 0 }, {
"coin": "FIFTY_PENCE",
"quantity": 0 }, {
"coin": "TWENTY_PENCE",
"quantity": 1 }, {
"coin": "TEN_PENCE",
"quantity": 2 }, {
"coin": "FIVE_PENCE",
"quantity": 2 }
]
}

4. API to get current coins quantity status from inventory
- Rest Url: http://localhost:8086/api/v1/coins/getInventory
- Request Type : Get mapping

5. API to clear coins in inventory for testing purpose
- Rest Url: http://localhost:8086/api/v1/coins/clearInventory
- Request Type : Delete mapping
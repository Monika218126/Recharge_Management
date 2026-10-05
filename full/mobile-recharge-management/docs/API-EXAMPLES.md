# API examples

## Health

```http
GET /api/health
```

## Customers

```http
GET /api/customers
```

```http
POST /api/customers
Content-Type: application/json

{
  "fullName": "Test User",
  "email": "test@example.com",
  "address": "Chennai"
}
```

## Mobile

```http
POST /api/mobiles
Content-Type: application/json

{
  "customerId": 1,
  "mobileNumber": "9888877777",
  "operatorName": "Jio",
  "circleName": "Tamil Nadu"
}
```

## Recharge

```http
POST /api/recharges
Content-Type: application/json

{
  "mobileId": 1,
  "planId": 1,
  "paymentMethod": "UPI"
}
```

The application generates a transaction reference and calls:

`CALL perform_recharge(...)`

The database trigger then updates the mobile number validity and status.

## Above average plans

```http
GET /api/plans/above-average-usage
```

# mortgage_service
Mortgage Service MVP

The service provides two endpoint:

- GET /api/interest-rates (get a list of current interest rates)
  Call example: 
  curl -H "Accept: application/json" http://localhost:8080/api/interest-rates

- POST /api/mortgage-check (post the parameters to calculate for a mortgage check)
  Call example: 
  curl -X POST http://localhost:8080/api/mortgage-check \
  -H "Content-Type: application/json" \
  -d '{"income":75000.00,"maturityPeriod":30,"loanValue":250000.00,"homeValue":300000.00}'

Service could be started on three environments - local/test/pro, appropriate configuration exists for each env.
For TEST and PRO environments it should be started in Docker environment, appropriate docker compose.yaml should be prepared.

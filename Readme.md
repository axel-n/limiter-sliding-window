# Rate limiter with sliding window algorithm

## Why another rate limiter?
- this rate limiter is based on the algorithm sliding window
- not found any solution in popular libraries (bucket4j, resilience4j, guava)

## Briefly about the problem
Some resources ([example1](https://www.bitmex.com/app/restAPI#Limits), [example2](https://binance-docs.github.io/apidocs/spot/en/#limits)) has some limits  for accept user requests.

So application need to know (fast, without sent packets to any additional system) - can they send request or wait or skip

## How it works
// (TODO ) add picture/schema about sliding window algorithm 


## Plans
- [ ] integrate deploy to maven repository
- [ ] add examples of usage
- [ ] add ability setup several limiters (for example 30 requests per 1 minute and 5 requests per second) 
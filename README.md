# GRPC Java Course

## Client streaming

- return `StreamObserver` -> clients send multiple requests + server handle these one by one

## BiDi Streaming API

- The client send `many` messages to the server and will receive `many` responses from the server.
- The number of requests and responses do not have to match.
- Bi Di Streaming RPC are well suited for:
  - When  the client and the server needs to send a lot of data asynchronously
  - `Chat` protocol
  - Long running connections

## Errors in gRPC

## Deadlines in gRPC

- Recommends set a deadline for all client RPC calls.
- Note: Deadlines are propagated across if gRPC calls are chained
  - A => B => C (deadline for A is passed to B and then passed to C)

## SSL Security

- grpc-netty-shaded: shaded means include `ssl` library
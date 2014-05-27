# Simple Example with Spray, Akka, and Squeryl

The basic setup is from the valuable [spray template](http://github.com/spray/spray-template).
The example covers
- REST-Services with [Spray](http://spray.io)
- an event-driven architecture based on [Akka](http://akka.io)
- persistence with [squeryl](http://squeryl.org)
- Testing with [Specs2](http://etorreborre.github.io/specs2/)

## Structure
The project layout is corresponding to the classic maven style directory structure. All source code is located
in src/main/scala and src/test/scala, respectively.

The source code is organized in the following packages:
- api: The rest service stuff, i.e. definition of routes, Json marshallers
- core: the rest agnostic business logic: message objects, operations
- domain: the domain model as case class(es)
- database: Squeryl schema definition, database configuration

## Basic data flow
1. a Request hits the defined routes in the webservice actor
2. the Request is converted to a message object
3. An intermediate Actor is created per request to handle the completion of the request (pre request pattern)
4. the Message object is passed to the business logic actor via the per request Actor
5. the business logic actor mainly triggers the database interaction
6. the resulting data is passed back to the per request actor which completes the request

## Performance

Performance and stability are pretty hard achieve in this setup. First I implemented the ask pattern, but the system
crashed under heavy load. That is not tolerable in any serious system. Fiddling with akka settings doesn't help (or I couln't
figure out how to configure everything correctly).

Then I switched to the pre request pattern. That solved the performance and stability problems for me as the blocking part
of the system is encapsulated in a separate Actor per request. The overall performance is quite good now. Reading access
gets up to 2000 req/s on a i5, which is quite nice.

## Building
The project is built using sbt. Use 
```
sbt gen-idea
```
to create Idea project files.

## License
The project is released under the MIT license.


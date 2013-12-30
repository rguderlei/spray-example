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
1. Request hits the defined routes in the webservice actor
2. Request is converted to a message object
3. Message object is passed to the business logic actor
4. the database interaction is triggered the message object
5. the resulting data is passed back to the webservice actor

## Known Issues

The performance (req/s) is not that great yet. 

## Building
The project is built using sbt. Use 
```
sbt gen-idea
```
to create Idea project files.

## License
The project is released under the MIT license.


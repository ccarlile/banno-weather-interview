# Weather App

This is a Scala 3 application that enables one to look up the current weather by Latitude and Longitude using the National Weather Service API.

## Running the App

Ensure SBT is installed. Run the app with `sbt run`

You can then call the API from the command line:
```
❯ curl 'http://localhost:8080/weather?lat=45.58&lon=-115.45'
{"shortForecast":"Mostly Sunny","characterization":"Cold"}%
```

## Project Structure

```
.
├── build.sbt
├── project
│   ├── build.properties
│   └── metals.sbt
├── README.md
└── src
    └── main
        └── scala
            └── weather
                ├── GetWeatherService.scala <- The "business logic"
                ├── Main.scala <- Main, logs that it's starting and invokes Server.apply()
                ├── Routes.scala <- Http routes
                ├── Server.scala <- Scaffolds HTTP client, loggers, routes & mounts server
                └── WeatherAPIClient.scala <- NWS Client
```

## Design Notes (Shortcuts)
This is a fairly flat package heirarchy, but with only a handful of files I didn't want to overcomplicate at this stage of the design. Were this app to develop further, I'd probably refactor this into a Ports-And-Adapters architecture. Tagless style maps fairly well to Ports-And-Adapters since the `trait`s correspond to ports and the `impl`s to the adapters. In this style, application code is separated from the application IO touchpoints like the HTTP API (input) and the HTTP Client (output) such that it only uses interfaces specified in the ports (this is why `Server` is implemented directly in `IO` - it's neither a port nor an adapter, it's core application code). This allows for clean separation of interfaces and implementations. As a simple step to ease this transision in the future, however, each of the HTTP Routes, Business logic service, and the NWS client provide their own (highly similar) domain models instead of a single type across all layers in the application. These types tag along in the same file as the service that uses them, and in a more mature application they'd live in their own files in the package heirarchy.

There is little in the way of "enterprise grade" here - I had already exceeded the allotted time when I had a working solution, so the error models on the NWS API aren't implemented and neither are the error state responses from this application. There's no observability other than logging requests and responses to console, and no configuration. In a more robust implementation I would have metrics and tracing, and a configuration that would read environment variables that specify e.g. logging configuration, the base URI of the weather API, whether to use a live or stub client, etc. There are also no tests - in an ideal application, there would be unit tests that check the proper handling of latitude and longitude parameters, that client requests are formatted properly, etc. There would also be integration tests that bring up a full set of Routes against a configurable NWS backend (to respect the API and avoid getting rate limited).

One final design note: I went back and forth several times on whether to implement `getForecast` in the `WeatherAPIClient` as one method or two. The NWS API works by first getting the grid coordinates from the latitude/longitude pair, then following the forecast URL for that grid coordinate. Though it's technically two calls,the second one will basically never get called on its own. Implementing it as one method means it doesn't leak implementation details to `GetWeatherService`.

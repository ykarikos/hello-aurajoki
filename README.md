# hello-aurajoki

The end result of some some live coding Clojure at
[Aurajoki Overflow](https://twitter.com/AurajokiO)
fall meetup on [Functional programming](https://meetabit.com/events/aurajoki-overflow-functional-programming).

I started at
[153fef8beb859c934a88b85c7558d90cc64ca16e](https://github.com/ykarikos/hello-aurajoki/tree/153fef8beb859c934a88b85c7558d90cc64ca16e)
and ended up live coding 
[32441cb74ef6c65ef6581f5558e1439af03f91b1](https://github.com/ykarikos/hello-aurajoki/commit/32441cb74ef6c65ef6581f5558e1439af03f91b1).

See the branch [live-coding](https://github.com/ykarikos/hello-aurajoki/tree/live-coding) 
for the things I had prepared beforehand.

## Prerequisities

Install
- Java
- [Leiningen](https://leiningen.org/)

## Setup

```sh
lein run
curl http://localhost:3000/?friend=Matias
```

Check the weather API at e.g.
http://localhost:3000/api/average-temperature/Turku

## Build

```sh
lein uberjar
java -jar target/hello-aurajoki.jar
```

## Next steps with Clojure

- The philosophy behind Clojure: 
  Watch Rich Hickey's talk [Simple Made Easy](https://www.youtube.com/watch?v=LKtk3HCgTa8)
- Learning Clojure: read the [Clojure for the Brave and True](https://www.braveclojure.com/) book
- Practical help: [Clojure Cheatsheet](https://clojure.org/api/cheatsheet)
- Community: [Clojure Finland](https://clojurefinland.github.io/)

## Me

```clojure
me
=>
{:name         "Ykä"
 :twitter      "@ykarikos"
 :presentation "Clojure superpower: REPL"
 :work         {:company "Digitaalinen asuntokauppa DIAS" 
                :role    "Engineering team lead"}
 :podcast      {:name    "Koodia pinnan alla"
                :url     "https://koodiapinnanalla.fi"}}
```

I'm Ykä ([@ykarikos](https://twitter.com/ykarikos)),
I build Clojure microservices and lead the engineering team at [Digitaalinen asuntokauppa DIAS](https://dias.fi/) and
co-host the [Koodia pinnan alla](https://koodiapinnanalla.fi/) podcast.


## License

Licensed with [MIT License](LICENSE).

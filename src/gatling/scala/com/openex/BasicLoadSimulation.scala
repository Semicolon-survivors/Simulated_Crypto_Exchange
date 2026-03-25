package com.openex

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicLoadSimulation extends Simulation {

  private val baseUrl: String =
    Option(System.getProperty("BASE_URL"))
      .orElse(Option(System.getenv("BASE_URL")))
      .getOrElse("http://localhost:8080")

  private val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json, text/plain, */*")
    .userAgentHeader("OpenEx-Gatling/1.0")

  private val healthScenario = scenario("Actuator Health")
    .exec(
      http("GET /actuator/health")
        .get("/actuator/health")
        .check(status.is(200))
        .check(jsonPath("$.status").is("UP"))
    )
    .pause(200.millis, 600.millis)

  private val prometheusScenario = scenario("Actuator Prometheus")
    .exec(
      http("GET /actuator/prometheus")
        .get("/actuator/prometheus")
        .check(status.is(200))
        .check(substring("jvm_memory_used_bytes"))
    )
    .pause(200.millis, 600.millis)

  setUp(
    healthScenario.inject(
      rampUsers(100).during(30.seconds),
      constantUsersPerSec(20).during(60.seconds)
    ),
    prometheusScenario.inject(
      rampUsers(50).during(30.seconds),
      constantUsersPerSec(10).during(60.seconds)
    )
  ).protocols(httpProtocol)
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.apache.usergrid.simulations

import io.gatling.core.Predef._
 import org.apache.usergrid.datagenerators.FeederGenerator
 import org.apache.usergrid.scenarios.UserScenarios
 import org.apache.usergrid.settings.Settings

 import scala.concurrent.duration._

class PostUsersSimulation extends Simulation {

  // Target settings
  val httpConf = Settings.httpConf

  // Simulation settings
  val numUsers:Int = Settings.numUsers
  val rampTime:Int = Settings.rampTime
  val throttle:Int = Settings.throttle

  // Geolocation settings
  val centerLatitude:Double = Settings.centerLatitude
  val centerLongitude:Double = Settings.centerLongitude
  val userLocationRadius:Double = Settings.userLocationRadius
  val geosearchRadius:Int = Settings.geosearchRadius

  val feeder = FeederGenerator.generateUserWithGeolocationFeeder(numUsers, userLocationRadius, centerLatitude, centerLongitude).queue

  val scnToRun = scenario("POST geolocated users")
    .feed(feeder)
    .exec(UserScenarios.postUserIfNotExists)

  setUp(scnToRun.inject(atOnceUsers(numUsers)).throttle(reachRps(throttle) in (rampTime.seconds)).protocols(httpConf))

}

package cc.sifter.api.controllers


import cc.sifter.api.requests.HiRequest
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import collection.mutable.{Map => MMap}

class HelloWorldController extends Controller {

  val db = MMap.empty[String, Any]

  get("/hi") { request: Request =>
    info("hi")
    "Hello " + request.params.getOrElse("name", "unnamed")
  }

  post("/hi") { hiRequest: HiRequest =>
    db.put(hiRequest.name, hiRequest.id)
    "Hello " + hiRequest.name + " with id " + hiRequest.id
  }
}

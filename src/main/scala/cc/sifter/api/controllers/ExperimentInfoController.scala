package cc.sifter.api.controllers

import javax.inject.Singleton

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

@Singleton
class ExperimentInfoController extends Controller {

  import cc.sifter.api.db.InMemoryDatabase.DB

  get("/list") { request: Request =>
    response.ok.json(Map("live_experiments" -> DB.keys))
  }

  get("/db") { request: Request =>
    response.ok.json(DB.mapValues(_.status))
  }

  get("/status/:id") { request: Request =>
    val id = request.getIntParam("id")
    DB.get(id)
      .map { experiment =>

        response.ok.json(experiment.status)
      }
      .getOrElse {
        val reason = s"experiment_id $id not found"
        response.notFound(Map("status" -> "failed", "reason" -> reason))
      }
  }

}

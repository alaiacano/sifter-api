package cc.sifter.api.controllers

import javax.inject.Singleton

import cc.sifter.api.db.Storage
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

@Singleton
class ExperimentInfoController @Inject() (DB: Storage) extends Controller {

  get("/db") { request: Request =>
    response.ok.json(DB.status)
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

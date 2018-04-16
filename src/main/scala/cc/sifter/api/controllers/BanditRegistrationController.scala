package cc.sifter.api.controllers

import cc.sifter.api.Experiment
import cc.sifter.{Arm, EpsilonGreedy, Selection}
import cc.sifter.api.requests._
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import collection.mutable.{Map => MMap}

class BanditRegistrationController extends Controller {

  val db = MMap.empty[Long, Experiment]

  /**
    * Creates a new experiment.
    *
    * Just give it a name and algo and number of arms:
    * curl -d '{"name":"adam", "algo":"epsilon_greedy", "n_arms": 3}' -H "Content-Type: application/json" -X POST http://localhost:8888/create
    *
    * Or provide a list of names for the arms.
    * curl -d '{"name":"adam2", "algo":"epsilon_greedy", "arms": ["moe", "larry", "curly"]}' -H "Content-Type: application/json" -X POST http://localhost:8888/create
    */
  post("/create") { request: FullCreateExperimentRequest =>
    val nextId = if (db.keys.nonEmpty) db.keys.max + 1 else 1L
    val arms = request.arms.map(Arm(_))
    val bandit = EpsilonGreedy(arms, request.epsilon)
    val exp = Experiment(nextId, request.name, bandit, false)

    db.put(nextId, exp)
    response.created.json(Map("status" -> "success", "data" -> exp.status))
  }

  get("/list") { request: Request =>
    response.ok.json(Map("live_experiments" -> db.keys))
  }

  get("/db") { request: Request =>
    response.ok.json(db.mapValues(_.status))
  }

  get("/status") { request: Request =>
    val success = for {
      id <- request.params.getLong("id")
      experiment <- db.get(id)
    } yield {
      response.ok.json(experiment.status)
    }

    success.getOrElse {
      val reason = request.params.getLong("id")
        .map(id => s"experiment_id $id not found")
        .getOrElse("Unknown")
      response.notFound(Map("status" -> "failed", "reason" -> reason))
    }
  }

  /**
    * Changes the active state of an experiment
    *
    * curl -d '{"id":1, "is_live": true}' -H "Content-Type: application/json" -X POST http://localhost:8888/activate
    *
    * Params are of type [[ActivateRequest]]
    */
  post("/activate") { actRequest: ActivateRequest =>
    db.get(actRequest.id)
      .map { exp =>
        db.put(actRequest.id, exp.copy(isLive=actRequest.isLive))
        db(actRequest.id).status
      }
  }

  /**
    * Post a reward for a given experiment/arm.
    *
    * curl -d '{"experiment_id":1, "arm_id": "a", "value": 3.2}' -H "Content-Type: application/json" -X POST http://localhost:8888/reward
    *
    * Params are of type [[RewardRequest]]
    */
  post("/reward") { rewardRequest: RewardRequest =>
    db.get(rewardRequest.experimentId)
      .map { exp =>
        val s = Selection(rewardRequest.armId, rewardRequest.value)
        exp.bandit.update(s)
        exp.status
      }
  }

  /**
    * Requests an arm to use of a given experiment. The arm's requestCount will be incremented with each requets to this route.
    *
    * curl -d '{"experiment_id":1}' -H "Content-Type: application/json" -X POST http://localhost:8888/pull
    *
    *
    */
  post("/pull") { pullRequest: PullRequest =>
    db.get(pullRequest.experimentId)
      .map(_.bandit.selectArm())
  }
}










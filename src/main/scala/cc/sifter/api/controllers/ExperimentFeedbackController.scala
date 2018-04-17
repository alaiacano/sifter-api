package cc.sifter.api.controllers

import javax.inject.{Inject, Singleton}

import cc.sifter.Selection
import cc.sifter.api.db.Storage
import cc.sifter.api.requests.{ActivateRequest, PullRequest, RewardRequest}
import com.twitter.finatra.http.Controller

@Singleton
class ExperimentFeedbackController @Inject() (DB: Storage) extends Controller {


  /**
    * Changes the active state of an experiment
    *
    * curl -d '{"id":1, "is_live": true}' -H "Content-Type: application/json" -X POST http://localhost:8888/activate
    *
    * Params are of type [[ActivateRequest]]
    */
  post("/activate") { actRequest: ActivateRequest =>
    DB.get(actRequest.id)
      .map { exp =>
        DB.put(actRequest.id, exp.copy(isLive=actRequest.isLive))
        DB.get(actRequest.id).map(_.status)
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
    DB.get(rewardRequest.experimentId)
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
    DB.get(pullRequest.experimentId)
      .map(_.bandit.selectArm())
  }

}

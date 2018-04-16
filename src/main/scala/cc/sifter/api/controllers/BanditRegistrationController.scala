package cc.sifter.api.controllers

import javax.inject.Singleton

import cc.sifter.api.Experiment
import cc.sifter.{Arm, EpsilonGreedy, Selection}
import cc.sifter.api.requests._
import com.twitter.finatra.http.Controller

@Singleton
class BanditRegistrationController extends Controller {
  import cc.sifter.api.db.InMemoryDatabase.DB


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
    val nextId = if (DB.keys.nonEmpty) DB.keys.max + 1 else 1L
    val arms = request.arms.map(Arm(_))
    val bandit = EpsilonGreedy(arms, request.epsilon)
    val exp = Experiment(nextId, request.name, bandit, false)

    DB.put(nextId, exp)
    response.created.json(Map("status" -> "success", "data" -> exp.status))
  }

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
        DB(actRequest.id).status
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










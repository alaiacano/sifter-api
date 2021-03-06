package cc.sifter.api.controllers

import javax.inject.{Inject, Singleton}

import cc.sifter.api.Experiment
import cc.sifter._
import cc.sifter.api.db.Storage
import cc.sifter.api.requests._
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder

@Singleton
class ExperimentRegistrationController @Inject() (DB: Storage) extends Controller {
  /**
    * Creates a new experiment.
    *
    * Just give it a name and algo and number of arms:
    * curl -d '{"name":"adam", "algo":"epsilon_greedy", "n_arms": 3}' -H "Content-Type: application/json" -X POST http://localhost:8888/create
    *
    * Or provide a list of names for the arms.
    * curl -d '{"name":"adam2", "algo":"epsilon_greedy", "arms": ["moe", "larry", "curly"]}' -H "Content-Type: application/json" -X POST http://localhost:8888/create
    */
  post("/create/epsilon_greedy") { request: CreateEGRequest =>
    val arms = request.arms.map(Arm(_))
    val bandit = EpsilonGreedy(arms, request.epsilon)
    createExperiment(request.name, bandit)
  }

  post("/create/annealing_epsilon_greedy") { request: CreateAnnealingEGRequest =>
    val arms = request.arms.map(Arm(_))
    val bandit = AnnealingEpsilonGreedy(arms)
    createExperiment(request.name, bandit)
  }

  post("/create/softmax") { request: CreateSoftMaxRequest =>
    val arms = request.arms.map(Arm(_))
    val bandit = SoftMax(arms, request.temperature)
    createExperiment(request.name, bandit)
  }

  post("/create/annealing_softmax") { request: CreateAnnealingSoftMaxRequest =>
    val arms = request.arms.map(Arm(_))
    val bandit = AnnealingSoftMax(arms)
    createExperiment(request.name, bandit)
  }

  post("/create/exp3") { request: CreateExp3Request =>
    val arms = request.arms.map(Arm(_))
    val bandit = Exp3(arms, request.gamma)
    createExperiment(request.name, bandit)
  }

  // helper to insert the new experiment into the database.
  private def createExperiment(name: String, bandit: Bandit): ResponseBuilder#EnrichedResponse = {
    val nextId = DB.nextId
    val exp = Experiment(nextId, name, bandit, isLive = false)

    DB.put(nextId, exp)
    response.created.json(Map("status" -> "success", "data" -> exp.status))
  }
}










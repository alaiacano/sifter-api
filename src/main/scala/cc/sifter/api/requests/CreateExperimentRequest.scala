package cc.sifter.api.requests

sealed trait CreateExperimentRequest {
  val algo: String
  val name: String
  val epsilon: Double
}

case class PartialCreateExperimentRequest(algo: String, name: String, nArms: Int, epsilon: Double)
case class FullCreateExperimentRequest(algo: String, name: String, arms: Seq[String], epsilon: Double)

case class ActivateRequest(id: Long, isLive: Boolean)

case class RewardRequest(experimentId: Long, armId: String, value: Double)

case class PullRequest(experimentId: Long)
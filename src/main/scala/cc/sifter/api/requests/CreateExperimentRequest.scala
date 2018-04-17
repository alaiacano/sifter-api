package cc.sifter.api.requests

case class CreateEGRequest(name: String, arms: Seq[String], epsilon: Double)
case class CreateAnnealingEGRequest(name: String, arms: Seq[String])

case class CreateSoftMaxRequest(name: String, arms: Seq[String], temperature: Double)
case class CreateAnnealingSoftMaxRequest(name: String, arms: Seq[String])

case class ActivateRequest(id: Long, isLive: Boolean)
case class RewardRequest(experimentId: Long, armId: String, value: Double)
case class PullRequest(experimentId: Long)
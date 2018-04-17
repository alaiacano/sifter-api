package cc.sifter.api.requests

sealed trait CreateBanditRequest {
  val name: String
  val arms: Seq[String]
}

case class CreateEGRequest(name: String, arms: Seq[String], epsilon: Double) extends CreateBanditRequest
case class CreateAnnealingEGRequest(name: String, arms: Seq[String]) extends CreateBanditRequest

case class CreateSoftMaxRequest(name: String, arms: Seq[String], temperature: Double) extends CreateBanditRequest
case class CreateAnnealingSoftMaxRequest(name: String, arms: Seq[String]) extends CreateBanditRequest

case class CreateExp3Request(name: String, arms: Seq[String], gamma: Double) extends CreateBanditRequest

case class ActivateRequest(id: Long, isLive: Boolean)
case class RewardRequest(experimentId: Long, armId: String, value: Double)
case class PullRequest(experimentId: Long)
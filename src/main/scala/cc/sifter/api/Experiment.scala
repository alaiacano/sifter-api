package cc.sifter.api

import cc.sifter.{Arm, Bandit}

case class Experiment(id: Long, name: String, bandit: Bandit, isLive: Boolean) {

  def status: Map[String, Any] = Map(
    "id" -> id,
    "name" -> name,
    "isLive" -> isLive,
    "arms" -> bandit.armsMap,
    "top_arm" -> topArm
  )

  def topArm: Arm = bandit.maxArm
}

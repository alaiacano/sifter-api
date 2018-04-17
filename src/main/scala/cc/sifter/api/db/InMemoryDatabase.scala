package cc.sifter.api.db

import cc.sifter.api.Experiment
import collection.mutable.{Map => MMap}

class InMemoryDatabase extends Storage {
  val DB = MMap.empty[Long, Experiment]

  var currentMaxId = 1L
  def get(id: Long): Option[Experiment] = DB.get(id)

  def put(id: Long, experiment: Experiment): Boolean = {
    DB.put(id, experiment)
    true
  }

  def nextId = {
    val id = currentMaxId
    currentMaxId += 1
    id
  }

  def status: Map[Long, Map[String, Any]] = DB.mapValues(_.status).toMap
}

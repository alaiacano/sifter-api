package cc.sifter.api.db

import cc.sifter.api.Experiment
import collection.mutable.{Map => MMap}

object InMemoryDatabase {
  val DB = MMap.empty[Long, Experiment]

}

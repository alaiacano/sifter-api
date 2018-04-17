package cc.sifter.api.db

import cc.sifter.api.Experiment

/**
  * Simple get/put interface for storing and retrieving Experiments.
  */
trait Storage {
  // Get an experiment by its ID
  def get(id: Long): Option[Experiment]

  // Save/update an experiment.
  def put(id: Long, experiment: Experiment): Boolean

  // The ID to be assigned to the next experiment that gets created.
  def nextId: Long

  // Some kind of status about the experiments. TODO: standardize this.
  def status: Map[Long, Map[String, Any]]
}

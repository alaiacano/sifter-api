package cc.sifter.api.db

import com.google.inject.{Provides, Singleton}
import com.twitter.app.Flag
import com.twitter.inject.TwitterModule

object DatabaseBackends {
  val IN_MEMORY = "InMemoryDatabase"
  // val REDIS = "REDIS"
  // val MEMCACHE = "MEMCACHE"
}

object DatabaseModule extends TwitterModule {
  val storageType: Flag[String] = flag(name = "storage", default = "InMemoryDatabase", help = "The kind of storage to use.")

  @Singleton
  @Provides
  def providesStorage: Storage = {
    storageType() match {
      case DatabaseBackends.IN_MEMORY =>
        new InMemoryDatabase
      case other =>
        throw new IllegalStateException(s"Unknown database backend: $other")
    }
  }
}
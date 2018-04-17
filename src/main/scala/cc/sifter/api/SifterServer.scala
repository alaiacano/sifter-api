package cc.sifter.api

import javax.inject.Inject

import cc.sifter.api.controllers._
import cc.sifter.api.db.DatabaseModule
import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter

object SifterServerMain extends SifterServer

class SifterServer @Inject() extends HttpServer {

  override def modules: Seq[Module] = super.modules ++ Seq(DatabaseModule)

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[ExperimentInfoController]
      .add[ExperimentRegistrationController]
      .add[ExperimentFeedbackController]
  }
}

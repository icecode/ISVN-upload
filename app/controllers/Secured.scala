package controllers

import play.api.mvc._
import models._

trait Secured {

  private def username(request: RequestHeader): Option[String] = request.session.get("username")


  def currentUser(implicit request:RequestHeader):Option[models.User] = {
    request.session.get("username") match {
      case Some(u) => User.findByUsername(u)
      case _ => None
    }
  }

  def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Application.index)
  }

  def IsAuthenticated(unAuthorized: RequestHeader => Result)(f: User => Request[AnyContent] => Result) = authenticated(unAuthorized,  f)

  def IsAuthenticated(f: User => Request[AnyContent] => Result) = authenticated(onUnauthorized,  f)

  def authenticated( unAuthorized: RequestHeader => Result,  f: User => Request[AnyContent] => Result) = {
    Security.Authenticated(username, unAuthorized){
      username => {
        Action(request => f(User.findByUsername(username).get)(request))
      }
    }
  }

}

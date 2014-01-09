package controllers

import play.api.mvc.WebSocket
import play.api.libs.iteratee._
import models._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-17
 * Time: 下午5:53
 */
object MessageApplication extends BaseController{

  def index() = WebSocket.using[String]{ implicit request =>
    val uname = currentUser match{
      case Some(cu) => cu.username
      case _ => "none"
    }
    val out = OnlineUser.get(uname)
    val in = Iteratee.foreach[String]{ s => s match {
      case _ => {
      }
    }}.mapDone { _ => println("close");}
    (in, out)
  }
}

package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsNumber, Json}
import _root_.models._

object Application extends Controller {


  lazy val loginForm = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(LoginCase.apply)(LoginCase.unapply).verifying("username", u =>  User.authenticate(u.username, u.password)
    )
  )

  def index() = Action { implicit request =>
    request.session.get("username") match{
      case Some(name) if !name.isEmpty => Redirect(routes.DemandApplication.index(1))
      case _ => Ok(views.html.index(loginForm))
    }
  }

  def login() = Action { implicit request =>
      loginForm.bindFromRequest.fold(
        error => Ok(Json.stringify(Json.obj("error" -> JsNumber(1)))),
        success => {
          Ok(Json.stringify(Json.obj("error" -> JsNumber(0)))).withSession("username" -> success.username)
        })
  }

  def logout() = Action{ implicit request =>
    Redirect(routes.Application.index()).withSession("username" -> "")
  }

  def updateLog() = Action{ implicit request =>
    val log =
      """
        |/**
        | * 上传系统更新记录
        | */
        |2013.7.9:
        |    1、重构Damend模型，去除util.Util, util.RsyncUploadProcess 2个util包， 增加Task包，rsync同步使用task.RsyncTask
        |
        |2013.5.16:
        |    1、修正回滚版本不正常BUG
        |
        |2013.2.17:
        |    1、上传文件列表筛选剔除删除文件
        |    2、增加有新上线单时刷新页面(仅负责上线权限的人会刷新)
        |
        |2013.2.7:
        |    1、增加RTX上传提示
        |
        |2013.3.28
        |    1、更新到play框架到play2.10
        |    2、更新scala到2.10
        |    3、UI 更新为google样式
        |
        |2013.5.6
        |    1、使用scala方式实现命令行 @util.RsyncUploadProces
        |
        |2013.5.7
        |    1、修改登录错误提示
        |
        |
      """.stripMargin
    Ok(log)
  }

}

case class LoginCase(username:String, password:String)
package controllers

import play.api.data.Form
import play.api.data.Forms._
import models._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-17
 * Time: 下午1:59
 */
object SettingApplication extends BaseController{

  val setProjectsForm = Form(
    tuple(
      "develProjects" -> text,
      "dp" -> text
    )
  )

  def project() = IsAuthenticated( currentUser => implicit request => {
    if(request.method == "POST"){
      setProjectsForm.bindFromRequest.fold(
        error => { Ok("setting error")},
        success => {
          currentUser.setDevelProjects(success._1)
          Redirect(routes.SettingApplication.project)
        }
      )
    }else{
      Ok(views.html.setting.project(currentUser.develProject, Project.findAll(), Some(currentUser)))
    }
  })
}

package controllers

import play.api.data.Form
import play.api.data.Forms._

import models._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-23
 * Time: 下午2:38
 */
case class UserCase(
                     username:String
                     , password:String
                     , email:String
                     , svnUsername:String
                     , svnPassword:String)
object UserApplication extends BaseController{

  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text,
      "email" -> text,
      "svnUsername" -> text,
      "svnPassword" -> text
    )(UserCase.apply)(UserCase.unapply)
  )

  def index() = IsAuthenticated( currentUser => request => {
    Ok(views.html.user.index(User.findAllDevelopers(), Some(currentUser)))
  })

  def post() = IsAuthenticated( currentUser => implicit request => {
    if (request.method == "POST"){
      userForm.bindFromRequest().fold(
        error => { Ok(error.errorsAsJson)},
        success => {
          User.create(success.username, success.password, success.email, success.svnUsername, success.svnPassword) match{
            case cr:User => {
              Redirect(routes.UserApplication.index())
            }
            case _ => {
              Ok("create error ")
            }
          }
        }
      )
    }else{
      Ok(views.html.user.post(Some(currentUser)))
    }
  })

  def delete(uid:Long) = IsAuthenticated( currentUser => implicit request => {
    User.findById(uid) match{
      case Some(cu) => {
        cu.destroy()
        Redirect(routes.UserApplication.index)
      }
      case _ => {
        Redirect(routes.UserApplication.index)
      }
    }
  })

  def edit(uid:Long) = IsAuthenticated( currentUser => implicit request => {
    User.findById(uid) match{
      case Some(cu) => {
        if (request.method == "POST"){
          userForm.bindFromRequest().fold(
            error => { Ok(error.errorsAsJson)},
            success => {
              val pasd = if (success.password.isEmpty) cu.password else success.password
              cu.updateData(success.username, pasd, success.email, success.svnUsername, success.svnPassword)
              Redirect(routes.UserApplication.index)
            }
          )
        }else{
          Ok(views.html.user.edit(cu, Some(currentUser)))
        }
      }
      case _ => {
        Redirect(routes.UserApplication.index)
      }
    }
  })
}

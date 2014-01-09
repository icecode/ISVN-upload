package controllers

import play.api.mvc._
import play.api.data.Form

import _root_.models._
import play.api.data.Forms._
import job._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-5
 * Time: 下午4:55
 */
case class DemandForm(title:String, projectId:Int, uploadFiles:String)

object DemandApplication extends BaseController{

  val demandForm = Form(
    mapping(
      "title" -> text,
      "projectId" -> number,
      "uploadFiles" -> text
    )(DemandForm.apply)(DemandForm.unapply)
  )

  def index(page:Int = 1) = IsAuthenticated( currentUser => implicit request =>
    Ok(views.html.demand.index(Demand.findPage(page), page, Some(currentUser)))
  )

  def show(did:Long) = IsAuthenticated( currentUser => implicit request =>
    Demand.findById(did) match{
      case Some(demand) => Ok(views.html.demand.show(demand))
      case _ => Ok("error")
    }
  )

  def post() = IsAuthenticated( currentUser => implicit request =>
    if(request.method == "POST"){
      demandForm.bindFromRequest.fold(
        error => Ok("post error:" + error),
        success => {
          Demand.create(currentUser, success.title, success.projectId, success.uploadFiles.split("\\|").map(f => (f.split(",")(0), f.split(",")(1).toLong)).toList)
          Results.Redirect(routes.DemandApplication.index(1))

      })

    }else{
      Ok(views.html.demand.post(User.findAllDevelopers(), currentUser.develProject, Some(currentUser)))
    }
  )


  def upload(did:Long) = IsAuthenticated( currentUser => implicit request =>
    Demand.findById(did) match {
      case Some(demand) if demand.status == Demand.STATUS_ADD || demand.status == Demand.UPLOAD_ERROR => {
          JobActor.uploadActor ! UploadDemand(demand)
          Ok("success")
      }
      case _ => Ok("error")
    }
  )


  def rollback(did:Long) = IsAuthenticated( currentUser => implicit request => {
    Demand.findById(did) match {
      case Some(demand) if Demand.UPLOAD_SUCCESS == demand.status || Demand.UPLOAD_SUCCESS == Demand.ROLLBACK_ERROR => {
        if(request.method == "POST"){
          JobActor.uploadActor ! RollbackDemand(demand)
          Ok("success")
        }else{
          Ok(views.html.demand.rollback(demand.rollbackFiles))
        }
      }
      case _ => Ok("error")
    }
  })

  def commit(did:Long) = IsAuthenticated( currentUser => implicit request =>
    Demand.findById(did) match {
      case Some(demand) => {
        Ok(views.html.demand.commit(demand))
      }
      case _ => Ok("error")
    }
  )


  def delete(did:Long) = IsAuthenticated( currentUser => implicit request =>
    Demand.findById(did) match {
      case Some(demand) => {
        demand.destroy()
        Ok("success")
      }
      case _ => Ok("error")
    }
  )
}

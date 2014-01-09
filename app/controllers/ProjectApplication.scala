package controllers

import play.api.data.Form
import play.api.data.Forms._
import models._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-9
 * Time: 上午11:17
 */

case class UploadServerCase(uploadType:String
                            ,ftpAddress:String
                            , ftpUsername:String
                             , ftpPassword:String
                             , ftpPort:String
                             , ftpRootDir:String
                             , rsyncAddress:String
                             , rsyncPort:String)
case class ProjectCase(name:String
                        ,svnAddress:String
                        , svnUsername:String
                        ,svnPassword:String)

object ProjectApplication extends BaseController{


  val uploadServerForm = Form(
    mapping(
      "uploadType" -> text,
      "ftpAddress" -> text
    , "ftpPort" -> text
    , "ftpUsername" -> text
    , "ftpPort" -> text
    , "ftpRootDir" -> text
    , "rsyncAddress" -> text
    , "rsyncPort" -> text
    )(UploadServerCase.apply)(UploadServerCase.unapply)
  )

  val projectForm = Form(
    mapping(
      "name" -> text,
      "svnAddress" -> text,
      "svnUsername" -> text,
      "svnPassword" -> text
    )(ProjectCase.apply)(ProjectCase.unapply)
  )

  def index() = IsAuthenticated(currentUser => implicit request =>
    Ok(views.html.project.index(Project.findAll(), Some(currentUser)))
  )

  def post() = IsAuthenticated(currentUser => implicit request => {
    if (request.method == "POST"){
      projectForm.bindFromRequest().fold(
        error => {
          Ok(error.errorsAsJson)
        },
        success => {
          Project.create(success.name, success.svnAddress, success.svnUsername, success.svnPassword)
          Redirect(routes.ProjectApplication.index())
        }
      )
    }else{
      Ok(views.html.project.post(Some(currentUser)))
    }
  })

  def edit(pid:Long) = IsAuthenticated( currentUser => implicit request => {
    Project.findById(pid) match{
      case Some(project) => {
        if (request.method == "POST"){
          projectForm.bindFromRequest().fold(
            error => {
              Ok(error.errorsAsJson)
            },
            success => {
              project.updateData(success.name, success.svnAddress, success.svnUsername, success.svnPassword)
              Redirect(routes.ProjectApplication.index())
            }
          )
        }else{
          Ok(views.html.project.edit( project, Some(currentUser)))
        }
      }
      case _ => { Ok("error") }
    }
  })

  def delete(pid:Long) = IsAuthenticated( currentUser => implicit request => {
    Project.findById(pid) match{
      case Some(project) => {
        project.destroy()
        Redirect(routes.ProjectApplication.index())
      }

      case _ => { Ok("error")}
    }
  })

  def addServer(pid:Long) = IsAuthenticated( currentUser => implicit request =>
    if (request.method == "POST"){
      uploadServerForm.bindFromRequest().fold(
        error => Ok(error.errorsAsJson),
        success => {
          Project.findById(pid) match {
            case Some(project) => {
              project.addRsyncUploadServer(success.rsyncAddress, success.rsyncPort)
              Redirect(routes.ProjectApplication.addServer(project.id))
            }
            case _ => Ok("project error")
          }
      })
    }else{
      Ok(views.html.project.addserver(Project.findById(pid).get.uploadServers, pid:Long, Some(currentUser)))
    }
  )


  def editServer(sid:Long) = IsAuthenticated( currentUser => implicit request => {
    UploadServer.findById(sid) match{
      case Some(server) => {
       if(request.method == "POST"){
         uploadServerForm.bindFromRequest().fold(
           error => Ok(error.errorsAsJson),
           success => {
             if(success.uploadType == UploadServer.FTP){
               server.updateFtp(success.ftpAddress, success.ftpPort, success.ftpUsername, success.ftpPassword, success.ftpRootDir)
             }else{
               server.updateRsync(success.rsyncAddress, success.rsyncPort)
             }
             Redirect(routes.ProjectApplication.addServer(server.projectId))
           })
       }else{
         Ok(views.html.project.editserver(server, Some(currentUser)))
       }
      }
      case _ => Ok("error")
    }
  })


  def deleteServer(sid:Long) = IsAuthenticated( currentUser => implicit request =>{
    UploadServer.findById(sid) match{
      case Some(server) => {
        server.destroy()
        Ok("success")
      }
      case _ => Ok("error")
    }
  })
}

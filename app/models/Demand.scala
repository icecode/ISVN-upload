package models

import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import java.io.{FileOutputStream, FileInputStream, File}
import org.tmatesoft.svn.core.{SVNDepth, SVNURL}
import org.tmatesoft.svn.core.wc.SVNRevision
import play.api.Play._
import task.{RsyncTask, TaskCallHandler}

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-7
 * Time: 下午5:53
 */
class Demand(
  val id:Long,
  val title:String,
  val status:String,
  @Column("project_id")
  val projectId:Long,
  val addtime:Long,
  val adduserid:Long,
  val adduser:String,
  var message:String) extends KeyedEntity[Long] {

  lazy val project = transaction{
    from(ModelsSchema.project)( p =>
      where(p.id === projectId)
      select(p)
    ).single
  }

  lazy val uploadFiles = transaction{
    from(ModelsSchema.uploadFile)( f =>
      where(f.demandId === id)
      select(f)
    ).toList
  }

  lazy val uploadServers = transaction{
    from(ModelsSchema.uploadServer)( s =>
      where(s.projectId === projectId)
      select(s)
    ).toList
  }

  lazy val rollbackFiles = transaction{
    uploadFiles.map( uf => {
      from(ModelsSchema.uploadFile)(mf =>
        where(mf.uploadVersion lt uf.uploadVersion and mf.uploadFile === uf.uploadFile and mf.projectId === projectId and mf.uploadStatus === 1)
          select(mf)
          orderBy(mf.id desc)
      ).headOption match{
        case Some(rf) => (uf.uploadFile, uf.uploadVersion, rf.uploadVersion)
        case _ => (uf.uploadFile, uf.uploadVersion, 0L)
      }
    })
  }

  lazy val formatDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def localSvnDir = configuration.getString("svn.checkout").get + "/" + projectId.toString + "/"

  def localCopyDir = configuration.getString("svn.copy").get + "/" + projectId.toString + "/"

  def isCheckout() = new File(localCopyDir).exists()

  private def checkoutToLocal() {
    if(!isCheckout()) {
      val file = new File(localSvnDir)
      val client = util.Svn.getUpdateClient(project.svnUsername, project.svnPassword)
      file.mkdirs()
      client.doCheckout(SVNURL.parseURIEncoded(project.svnAddress), file, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true)
    }
  }

  private def cleanLocalCopyDir() {
    FileUtil.deleteFile(new File(localCopyDir))
  }



  private def copyUploadToLocate() {
    cleanLocalCopyDir()
    checkoutToLocal()
    val client = util.Svn.getUpdateClient(project.svnUsername, project.svnPassword)
    client.doUpdate(new File(localSvnDir), SVNRevision.HEAD, SVNDepth.INFINITY, true, true)
    uploadFiles.foreach( f => {
      val uploadFile = new File(localSvnDir + f.uploadFile)
      client.doUpdate(new File(localSvnDir + f.uploadFile), SVNRevision.create(f.uploadVersion), SVNDepth.INFINITY, true, true)
      if(!uploadFile.isDirectory){
        val copyFileName = localCopyDir + f.uploadFile
        val copyDirName = new File(copyFileName.substring(0, copyFileName.lastIndexOf("/")))
        copyDirName.mkdirs()
        FileUtil.copyFile(uploadFile, new File(copyFileName))
      }
    })
  }

  private def copyRollbackToLocate() {
    cleanLocalCopyDir()
    checkoutToLocal()
    val client = util.Svn.getUpdateClient(project.svnUsername, project.svnPassword)
    rollbackFiles.foreach( f => {
      if(f._3 > 0){
        val uploadFile = new File(localSvnDir + f._1)
        client.doUpdate(new File(localSvnDir + f._1), SVNRevision.create(f._3), SVNDepth.INFINITY, true, true)
        if(!uploadFile.isDirectory){
          val copyFileName = localCopyDir + f._1
          val copyDirName = new File(copyFileName.substring(0, copyFileName.lastIndexOf("/")))
          copyDirName.mkdirs()
          FileUtil.copyFile(uploadFile, new File(copyFileName))
        }
      }
    })
  }

  def uploadToServer() {
    startUpload()
    copyUploadToLocate()
    uploadServers.foreach( server => {
      new RsyncTask(localCopyDir, server.rsyncAddress, server.rsyncPort, uploadHandler()).start()
    })
  }

  def rollbackToServer() {
    startRollback()
    copyRollbackToLocate()
    uploadServers.foreach( server => {
      new RsyncTask(localCopyDir, server.rsyncAddress, server.rsyncPort, rollbackHandler()).start()
    })
  }


  /**
   * 返回一个回滚事件句柄
   *
   * @return TaskCallHandler
   */
  private def uploadHandler() = new TaskCallHandler {
    def onError(e: Throwable) {
      uploadServerMessage(Demand.UPLOAD_ERROR, "<br /><span class=\"label label-important\"> 同步失败</span>" + e.getMessage)
      val broadCastUsers = adduser :: configuration.getString("broadcastAdmin").get.split(",").toList
      OnlineUser.broadcastToUsers(broadCastUsers, "{event:'uploadError', msg:'%s 同步失败,请查看需求单详情了解详细信息!', did:%d}".format(title, id))
    }

    def onComplete(result: String) {
      uploadServerMessage(Demand.UPLOAD_SUCCESS, "<br /><span class=\"label label-success\"> 同步成功</span>" + result)
      val broadCastUsers = List(adduser)
      OnlineUser.broadcastToUsers(broadCastUsers, "{event:'uploadSuccess', msg:'%s 同步成功!', did:%d}".format(title, id))
    }
  }

  /**
   * 返回一个回滚事件句柄
   *
   * @return TaskCallHandler
   */
  private def rollbackHandler() = new TaskCallHandler {
    def onError(e: Throwable) {
      uploadServerMessage(Demand.ROLLBACK_ERROR, "<br /><span class=\"label label-important\"> 回滚失败</span>" + e.getMessage)
      val broadCastAdmin = configuration.getString("broadcastAdmin").get.split(",").toList ::: adduser :: Nil
      OnlineUser.broadcastToUsers(broadCastAdmin, "{event:'rollbackError', msg:'%s 回滚失败,请查看需求单详情了解详细信息!', did:%d}".format(title, id))
    }

    def onComplete(result: String) {
      uploadServerMessage(Demand.ROLLBACK_SUCCESS, "<br /><span class=\"label label-success\"> 回滚成功</span>" + result)
      val broadCastAdmin = adduser :: Nil
      OnlineUser.broadcastToUsers(broadCastAdmin, "{event:'rollbackSuccess', msg:'%s 回滚成功!', did:%d}".format(title, id))
    }

  }


  private def startUpload() {
    message += "<br /><div class=\"alert alert-success\">开始上传 %s</div>".format(formatDate.format(new java.util.Date()))
    transaction{
      update(ModelsSchema.demand)( d =>
        where(d.id === id)
          set(d.message := message, d.status := Demand.STATUS_UPLOADING)
      )
    }
  }

  private def startRollback() {
    message += "<br /><div class=\"alert alert-error\">开始回滚 %s</div>".format(formatDate.format(new java.util.Date()))
    transaction{
      update(ModelsSchema.demand)( d =>
        where(d.id === id)
          set(d.message := message, d.status := Demand.STATUS_UPLOADING)
      )
    }
  }

  def uploadServerMessage(s:String, msg:String){
    synchronized{
      message += msg
      transaction{
        update(ModelsSchema.demand)( d =>
          where(d.id === id)
            set(d.message := message, d.status := s)
        )
        update(ModelsSchema.uploadFile)( f =>
          where(f.demandId === id)
            set(f.uploadStatus := 1)
        )
      }
    }
  }

  def destroy(){ transaction{
    ModelsSchema.demand.deleteWhere( d => d.id === id)
    ModelsSchema.uploadFile.deleteWhere( uf => uf.demandId === id)
    }
  }
}


object Demand{

  val STATUS_ADD       = "add"
  val STATUS_UPLOADING = "uploading"

  val UPLOAD_ERROR     = "error"
  val UPLOAD_SUCCESS   = "success"

  val ROLLBACK_ERROR   = "rollbackerror"
  val ROLLBACK_SUCCESS = "rollbacksuccess"

  def findById(did:Long) = transaction{
    from(ModelsSchema.demand)(d =>
      where(d.id === did)
      select(d)
    ).headOption
  }


  def create(user:User, title:String, projectId:Long, uploadFiles:List[(String,Long)]) = transaction{
    ModelsSchema.demand.insert(new Demand(0L, title, STATUS_ADD, projectId, System.currentTimeMillis()/1000, user.id, user.username, "")) match{
      case demand:Demand => {
        ModelsSchema.uploadFile.insert(uploadFiles.map(f => new UploadFile(0, demand.id, demand.projectId, f._1, f._2, 0)))
        val broadCastAdmin = configuration.getString("broadcastAdmin").get.split(",").toList
        OnlineUser.broadcastToUsers(broadCastAdmin, "{event:'newDemand', msg:'%s提交了上线单 %s'}".format(user.username, title))
      }
      case _ => None
    }
  }

  def findPage(page:Int, pageSize:Int = 20) = transaction{
    from(ModelsSchema.demand)( d =>
      select(d)
      orderBy(d.id desc)
    ).page((page-1)*pageSize, pageSize).toList
  }









}

object FileUtil{

  def copyFile(sourceFile: File, targetFile: File) = {
    val inputStream = new FileInputStream(sourceFile)
    val outputStream = new FileOutputStream(targetFile)

    val sourceFileChannel = inputStream.getChannel
    val outputFileChannel = outputStream.getChannel
    try{
      val size = sourceFileChannel.size()
      sourceFileChannel.transferTo(0L, size, outputFileChannel)
    }catch{
      case e:Throwable =>
    } finally {
      sourceFileChannel.close()
      outputFileChannel.close()
      inputStream.close()
      outputStream.close()
    }
  }

  def deleteFile(dir:File){
    if(dir.exists()){
      try{
        dir.listFiles().map{ f =>
          if(f.isDirectory){
            deleteFile(f)
          }
          f.delete()
        }
      }catch{
        case e:java.io.IOException =>
      }
    }
  }
}

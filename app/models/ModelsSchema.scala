package models

import org.squeryl._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-5
 * Time: 下午2:57
 */
object ModelsSchema extends Schema{

  lazy val user = table[User]("dev_user")

  val project  = table[Project]("dev_project")

  val demand   = table[Demand]("dev_demand")

  val uploadFile = table[UploadFile]("dev_uploadfile")

  val uploadServer = table[UploadServer]("dev_upload_server")

}
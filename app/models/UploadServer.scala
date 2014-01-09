package models

import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-9
 * Time: 下午2:53
 */
class UploadServer(
  val id:Long
  ,@Column("project_id")
   val projectId:Long
  ,@Column("upload_type")
    val uploadType:String
  ,@Column("ftp_address")
    val ftpAddress:String
  ,@Column("ftp_username")
    val ftpUsername:String
  ,@Column("ftp_password")
    val ftpPassword:String
  , @Column("ftp_port")
    val ftpPort:String
  , @Column("ftp_root_dir")
    val ftpRootDir:String
  , @Column("rsync_address")
    val rsyncAddress:String
  , @Column("rsync_port")
    val rsyncPort:String) extends KeyedEntity[Long]{


  def updateFtp(address:String, port:String, username:String, password:String, rootDir:String) = transaction{
    update(ModelsSchema.uploadServer)(s =>
      where(s.id === id)
      set(s.uploadType := UploadServer.FTP, s.ftpAddress := address, s.ftpPort := port , s.ftpUsername := port, s.ftpPassword := password, s.ftpRootDir := rootDir)
    )
  }

  def updateRsync(address:String, port:String) = transaction{
      update(ModelsSchema.uploadServer)(s =>
        where(s.id === id)
          set(s.uploadType := UploadServer.RSYNC, s.rsyncAddress := address, s.rsyncPort := port)
      )
  }

  def destroy(){
    transaction(
      ModelsSchema.uploadServer.deleteWhere(s => s.id === id)
    )
  }

}

object UploadServer{

  val TYPE_RSYNC = "rsync"

  val TYPE_FTP   = "ftp"

  val FTP = "ftp"
  val RSYNC = "rsync"

  def findById(id:Long) = transaction(
    from(ModelsSchema.uploadServer)(s =>
      where(s.id === id)
      select(s)
    ).headOption
  )

}

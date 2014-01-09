package models

import org.squeryl.annotations.Column
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-7
 * Time: 上午10:41
 */
class Project(
  val id:Long,
  val name:String,
  @Column("svn_address")
  val svnAddress:String,
  @Column("svn_username")
  val svnUsername:String,
  @Column("svn_password")
  val svnPassword:String,
  val addTime:Long,
  val uploadTime:Long
               ) extends KeyedEntity[Long]{

  def addRsyncUploadServer(rsyncAddress:String, rsyncPort:String) = {
    transaction{
      ModelsSchema.uploadServer.insert( new UploadServer(0, id, UploadServer.TYPE_RSYNC, "", "", "", "", "", rsyncAddress, rsyncPort))
    }
  }

  lazy val uploadServers = transaction{
    from(ModelsSchema.uploadServer)( s =>
      where(s.projectId === id)
      select(s)
    ).toList
  }

  def updateData(ename:String, esvnAddress:String, esvnUsername:String, esvnPassword:String) = transaction{
    update(ModelsSchema.project)( p =>
      where(p.id === id)
      set(p.name := ename, p.svnAddress := esvnAddress, p.svnUsername := esvnUsername, p.svnPassword := esvnPassword)
    )
  }

  def destroy(){
    transaction{
      ModelsSchema.project.deleteWhere( p => p.id === id)
      ModelsSchema.uploadServer.deleteWhere( p => p.projectId === id)
    }
  }

}

object Project{

  /*def findUserDevelop(user:User) = transaction{
    from(Schema.project)(p =>
      where(p.)
    )
  }*/

  def findById(id:Long) = transaction{
    from(ModelsSchema.project)( p =>
      where(p.id === id)
      select(p)
    ).headOption
  }

  def findAll() = transaction{
    from(ModelsSchema.project)(p =>
      select(p)
      orderBy(p.id desc)
    ).toList
  }

  def create(name:String, svnAddress:String, svnUsername:String, svnPassword:String) = transaction{
    ModelsSchema.project.insert(new Project(0, name, svnAddress, svnUsername, svnPassword, System.currentTimeMillis()/1000, 0L))
  }
}


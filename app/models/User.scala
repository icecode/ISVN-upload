package models

import org.squeryl.annotations.Column
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-5
 * Time: 下午3:32
 */
class User(
  val id:Long,
  val username:String,
  val password:String,
  val email:String,
  val projects:String,
  @Column("svn_username")
  val svnUsername:String,
  @Column("svn_password")
  val svnPassword:String,
  val perm:String) extends KeyedEntity[Long]{


  lazy val develProject = transaction{
    from(ModelsSchema.project)(p =>
      where(p.id in(projects.split(",").map( _.toLong).toList.toTraversable))
      select(p)
    ).toList
  }

  def setDevelProjects(p:String) = transaction{
    update(ModelsSchema.user)(u =>
      where(u.id === id)
      set(u.projects := p)
    )
  }

  def updateData(username:String, password:String, email:String, svnUsername:String, svnPassword:String) = transaction{
    update(ModelsSchema.user)( u =>
      where(u.id === id)
      set(u.username := username, u.password := password, u.email := email, u.svnUsername := svnUsername, u.svnPassword := svnPassword)
    )
  }

  def destroy(){
    transaction{
      ModelsSchema.user.deleteWhere( cu => cu.id === id)
    }
  }
}

object User{

  def create(username:String, password:String, email:String, svnUsername:String, svnPassword:String) = transaction{
    ModelsSchema.user.insert(new User(0, username, password, email, "1", svnUsername, svnPassword, ""))
  }

  def findById(id:Long) = transaction{
    from(ModelsSchema.user)( a =>
      where(a.id === id)
      select(a)
    ).headOption
  }

  def findByUsername(username:String) = transaction{
    from(ModelsSchema.user)( u =>
      where(u.username === username)
      select(u)
    ).headOption
  }


  def findAllDevelopers() = transaction{
    from(ModelsSchema.user)( u =>
      select(u)
    ).toList
  }

  def authenticate(username:String, password:String) = !transaction{
    from(ModelsSchema.user)( u =>
      where(u.username === username and u.password === password)
      select(u)
    ).isEmpty
  }
}



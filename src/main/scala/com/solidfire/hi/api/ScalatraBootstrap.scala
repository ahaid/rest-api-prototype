

import javax.servlet.ServletContext

import com.fasterxml.jackson.databind.ObjectMapper
import com.solidfire.hi.api.controllers._
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    implicit val objectMapper = new ObjectMapper
    try {
      context mount (new VolumeApi, "/volume/*")
      context mount (new AccountApi, "/account/*")
      context mount (new VolumeAccessGroupApi, "/volumeAccessGroup/*")
      context mount (new AuthApi, "/auth/*")
      context mount (new DocsApi, "/api-docs/*")
    } catch {
      case e: Throwable => e.printStackTrace()
    }
  }
}
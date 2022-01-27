package example

import zhttp.http._
import zhttp.service.Server
import zio._
object HelloWorld extends App {

  // Create HTTP route
  val app = Http.collectZIO[Request] { case req =>
    req.getBodyAsString.map { content =>
      Response.text(content)
    // Response(data = HttpData.fromStreamByteBuf(ZStream.fromQueue(content)))
    }
  }

  // Run it like any simple app
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server.start(8090, app.silent).exitCode
}

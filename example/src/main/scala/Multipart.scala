import zhttp.experiment.multipart.{BodyEnd, ChunkedData}
import zhttp.http._
import zhttp.service.Server
import zio.stream.{UStream, ZStream}
import zio.{App, ExitCode, URIO}

object Multipart extends App {
  def app: HttpApp[Any, Throwable] = HttpApp.fromHttp {
    Http.collectM[Request] { case req =>
      req.decodeContent(ContentDecoder.multipartDecoder(req)).map { content =>
        Response(data =
          HttpData.fromStream(
            ZStream
              .fromQueue(content)
              .takeUntil(_ == BodyEnd)
              .filter(_.isInstanceOf[ChunkedData])
              .asInstanceOf[UStream[ChunkedData]]
              .map(_.chunkedData)
              .mapChunks(_.flatten),
          ),
        )
      }
    }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server.start(8090, app).exitCode
}

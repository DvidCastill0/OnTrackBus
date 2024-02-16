using RoutesGoogleMapsScraper.Server;
using Serilog;

namespace RoutesGoogleMapsScraper.Controllers
{
    public static class CORSController
    {
        public static Response getCORSResponse(ILogger logger, Request request)
        {
            if (request != null)
                return new Response("CORS headers returned", 200);

            logger.Error("Request is empty!");
            return ErrorController.getNotAcceptable(logger, "Request must not be empty");
        }
    }
}

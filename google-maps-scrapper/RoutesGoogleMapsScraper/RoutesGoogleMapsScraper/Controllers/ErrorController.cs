using Newtonsoft.Json;
using RoutesGoogleMapsScraper.Server;
using Serilog;

namespace RoutesGoogleMapsScraper.Controllers
{
    public static class ErrorController
    {
        public static Response getNotFound(ILogger logger, string message = "Not found")
        {
            return GetResponse(logger, 404, "NotFound", message);
        }

        public static Response getMethodNotAllowed(ILogger logger, string message = "Method not allowed")
        {
            return GetResponse(logger, 405, "MethodNotAllowed", message);
        }

        public static Response getNotAcceptable(ILogger logger, string message = "Not acceptable")
        {
            return GetResponse(logger, 406, "NotAcceptable", message);
        }

        public static Response getBadRequest(ILogger logger, string message = "Bad request")
        {
            return GetResponse(logger, 400, "BadRequest", message);
        }

        private static Response GetResponse(ILogger logger, int statusCode, string statusName, string message)
        {
            logger.Debug($"Sending ({statusCode}) {statusName} response - Message: \"{message}\"");
            return new Response(JsonConvert.SerializeObject(new ResponseMessageError(false, message)), statusCode);
        }
    }
}

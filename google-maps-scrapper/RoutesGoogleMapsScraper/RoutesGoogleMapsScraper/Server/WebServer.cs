using Serilog;
using RoutesGoogleMapsScraper.Controllers;
using AutoMapper;

namespace RoutesGoogleMapsScraper.Server
{
    public class WebServer : IWebServer
    {
        private readonly string _baseURL;
        private bool _running;
        private readonly string[] _allowHeaders;
        private readonly string[] _allowMethods;

        public WebServer(string baseURL)
        {
            _baseURL = baseURL;
            _running = true;
            _allowHeaders = new string[] {
                "Content-Type",
                "Accept",
                "Accept-Encoding",
                "Accept-Language",
                "Access-Control-Request-Headers",
                "Access-Control-Request-Method",
                "Connection",
                "Origin",
                "Sec-Fetch-Dest",
                "Sec-Fetch-Mode",
                "Sec-Fetch-Site",
                "User-Agent"
            };
            _allowMethods = new string[]
            {
                "POST",
                "OPTIONS"
            };
        }

        public void Start(ILogger logger, IMapper mapper)
        {
            using (System.Net.HttpListener listener = new System.Net.HttpListener())
            {
                listener.Prefixes.Add(_baseURL);
                listener.Start();
                int requests = 0;
                logger.Information($"Listening on \"{_baseURL}\"...");

                while (_running)
                {
                    var context = listener.GetContext();
                    var taskFactory = new TaskFactory();

                    requests++;
                    var request = new Request(context.Request, requests);
                    logger.Debug(request.GetRequestData());

                    var bodyTask = request.GetBody();

                    var task = taskFactory.StartNew(() =>
                    {
                        request.Controller = "";
                        request.Endpoint = "";

                        if (context.Request?.Url?.Segments.Length > 1)
                        { 
                            request.Controller = context.Request.Url.Segments[1].Replace("/", "");

                            if (context.Request?.Url?.Segments.Length > 2)
                            { 
                                request.Endpoint = context.Request.Url.Segments[2].Replace("/", "");
                            }
                        }


                        using (var response = context.Response)
                        {
                            string body = bodyTask.Result;

                            var resp = request.GetMethod() switch
                            {
                                "POST" => SendDataToController(logger, mapper, request),
                                "OPTIONS" => CORSController.getCORSResponse(logger, request),
                                _ => ErrorController.getMethodNotAllowed(logger, $"{request.GetMethod()} method not allowed")
                            };

                            if (request.GetMethod().Equals("OPTIONS"))
                            {
                                response.Headers.Set("Access-Control-Allow-Headers", string.Join(",", _allowHeaders));
                                response.Headers.Set("Access-Control-Allow-Methods", string.Join(",", _allowMethods));
                                response.Headers.Set("Access-Control-Max-Age", "1728000");
                            }

                            response.Headers.Set("Content-Type", "application/json");
                            response.Headers.Set("Access-Control-Allow-Origin", "*");
                            var buffer = System.Text.Encoding.UTF8.GetBytes(resp.Content);
                            response.ContentLength64 = buffer.Length;
                            response.StatusCode = resp.StatusCode;
                            
                            using (var ros = response.OutputStream)
                            {
                                ros.Write(buffer, 0, buffer.Length);
                            }
                        }
                    });
                }
            }
        }

        public Response SendDataToController(ILogger logger, IMapper mapper, Request request)
        {
            return request.Controller switch
            {
                "OnTrackBus" => OnTrackBusController.ProcessRequest(logger, mapper, request),
                //"Directions" => DirectionsController.ProcessRequest(logger, mapper, request),
                _ => ErrorController.getNotFound(logger, "Controller not found")
            };
        }

        public void Stop()
        {
            _running = false;
        }
    }
}

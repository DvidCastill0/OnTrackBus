using Serilog;
using System.Diagnostics;

namespace RoutesGoogleMapsScraper.APIs
{
    public class OnTrackBusAPI
    {
        private readonly ILogger _logger;
        private Uri _url;
        private string _scheme;
        private string _host;
        private int _port;
        private string _resource;


        public OnTrackBusAPI()
        {
            _scheme = "https";
            _host = "ontrackbus.com";
            _port = 443;
            _resource = ".json";
        }

        public OnTrackBusAPI(ILogger logger)
        {
            _logger = logger;
            _scheme = "https";
            _host = "ontrackbus.com";
            _port = 443;
            _resource = ".json";
        }

        public OnTrackBusAPI(ILogger logger, string scheme) : this(logger)
        {
            _scheme = !string.IsNullOrWhiteSpace(scheme) ? scheme : "https";
        }

        public OnTrackBusAPI(ILogger logger, string scheme, string host) : this(logger, scheme)
        {
            _host = !string.IsNullOrWhiteSpace(host) ? host : "ontrackbus.com";
        }

        public OnTrackBusAPI(ILogger logger, string scheme, string host, int port) : this(logger, scheme, host)
        {
            try 
            {
                _port = Convert.ToInt32(port);
            }
            catch(Exception ex)
            {
                _port = 443;
                _logger.Error($"Port number must be a valid port number between 1 and 65,535 - Exception: \"{ex.Message}\"");
            }
        }

        public OnTrackBusAPI(ILogger logger, string scheme, string host, int port, string resource = ".json") : this(logger, scheme, host, port)
        {
            _resource = !string.IsNullOrWhiteSpace(resource) ? resource : ".json";
        }

        public void BuildUri()
        {
            _logger.Debug($"Building URI for OnTrackBus API");
            var uriBuilder = new UriBuilder();

            uriBuilder.Scheme = !string.IsNullOrWhiteSpace(_scheme) ? _scheme : "https";
            uriBuilder.Host = !string.IsNullOrWhiteSpace(_host) ? _host : "ontrackbus.com";
            uriBuilder.Port = _port;
            uriBuilder.Path = !string.IsNullOrWhiteSpace(_resource) ? _resource : ".json";

            _url = uriBuilder.Uri;
            _logger.Debug($"Uri for OnTrackBus API set as {uriBuilder}");
        }

        public string GetResource()
        {
            if (_url == null)
                BuildUri();

            _logger.Information("OnTrackBus resource extraction process started");
            var stopWatch = new Stopwatch();
            using (var client = new HttpClient())
            {
                stopWatch.Start();
                _logger.Information("Extracting resource information from OnTrackBus");
                var response = client.GetStringAsync(_url);

                response.Wait();
                stopWatch.Stop();
                int responseSize = !string.IsNullOrWhiteSpace(response.Result) ? response.Result.Length : 0;
                stopWatch.Stop();
                _logger.Information($"OnTrackBus resource extraction done in {stopWatch.ElapsedMilliseconds} ms. Resource size: {responseSize} bytes");
                return response.Result;
            }
        }
    }
}

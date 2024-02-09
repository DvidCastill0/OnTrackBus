using RoutesGoogleMapsScraper.Models;
using System.Text.Json;

namespace RoutesGoogleMapsScraper.APIs
{
    public class OnTrackBusAPI
    {
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

        public OnTrackBusAPI(string scheme)
        {
            _scheme = !string.IsNullOrWhiteSpace(scheme) ? scheme : "https";
        }

        public OnTrackBusAPI(string scheme, string host) : this(scheme)
        {
            _host = !string.IsNullOrWhiteSpace(host) ? host : "ontrackbus.com";
        }

        public OnTrackBusAPI(string scheme, string host, int port) : this(scheme, host)
        {
            try 
            {
                _port = Convert.ToInt32(port);
            }
            catch(Exception ex)
            {
                _port = 443;
            }
        }

        public OnTrackBusAPI(string scheme, string host, int port, string resource = ".json") : this(scheme, host, port)
        {
            _resource = !string.IsNullOrWhiteSpace(resource) ? resource : ".json";
        }

        public void BuildUri()
        { 
            var uriBuilder = new UriBuilder();

            uriBuilder.Scheme = !string.IsNullOrWhiteSpace(_scheme) ? _scheme : "https";
            uriBuilder.Host = !string.IsNullOrWhiteSpace(_host) ? _host : "ontrackbus.com";
            uriBuilder.Port = _port;
            uriBuilder.Path = !string.IsNullOrWhiteSpace(_resource) ? _resource : ".json";

            _url = uriBuilder.Uri;
        }

        public string GetResource()
        {
            if (_url == null)
                BuildUri();

            var content = string.Empty;

            using (var client = new HttpClient())
            {
                var response = client.GetStringAsync(_url);

                response.Wait();

                return response.Result;
            }
        }
    }
}

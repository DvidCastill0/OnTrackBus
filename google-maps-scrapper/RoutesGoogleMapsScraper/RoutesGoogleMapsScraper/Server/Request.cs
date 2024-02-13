using System.Net;
using System.Text;

namespace RoutesGoogleMapsScraper.Server
{
    public class Request : IRequest
    {
        #region private members
        private readonly HttpListenerRequest _request;
        private readonly int _sequence;
        private readonly string _body;
        private readonly string _method;
        private string _controller;
        private string _endpoint;
        private Guid _guid;
        #endregion

        #region constructors
        public Request(HttpListenerRequest request, int sequence)
        {
            _request = request;
            _sequence = sequence;
            _guid = System.Guid.NewGuid();
            _method = request.HttpMethod;
            _body = GetBody().Result;
            _controller = string.Empty;
            _endpoint = string.Empty;
        }

        public async Task<string> GetBody()
        {
            if (_body != null)
                return _body;

            var bodyStream = _request.InputStream;
            var bodyData = new byte[_request.ContentLength64];

            await bodyStream.ReadAsync(bodyData, 0, (int)_request.ContentLength64);

            return Encoding.UTF8.GetString(bodyData);
        }


        public string GetMethod() => _method;

        public string Controller
        {
            set => _controller = !string.IsNullOrWhiteSpace(value) ? value : string.Empty;
            get => _controller;
        }

        public string Endpoint
        {
            set => _endpoint = !string.IsNullOrWhiteSpace(value) ? value : string.Empty;
            get => _endpoint;
        }

        public int Sequence => _sequence;
        public string Guid => _guid.ToString();

        public List<Header> GetHeaders() => _request.Headers.AllKeys.SelectMany(_request.Headers.GetValues, (key, value) => new Header(key, value)).ToList();

        public string GetRequestData()
        {
            var stringBuilder = new StringBuilder();

            stringBuilder.AppendLine($"\nRequest #{_sequence} (GUID: {_guid})");
            stringBuilder.AppendLine("\nHeaders:{");

            foreach (var header in GetHeaders())
                stringBuilder.AppendLine($"\t{header.Key}: \"{header.Value}\";");

            stringBuilder.AppendLine("}");

            stringBuilder.AppendLine($"URL: \"{_request.Url}\"");
            stringBuilder.AppendLine($"Method: {_method}");


            stringBuilder.AppendLine($"body: {{\"{_body}\"}}");

            return stringBuilder.ToString();
        }
        #endregion
    }
}

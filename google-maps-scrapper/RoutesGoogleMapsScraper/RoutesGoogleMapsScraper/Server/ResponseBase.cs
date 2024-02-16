namespace RoutesGoogleMapsScraper.Server
{
    public class ResponseBase
    {
        private readonly string _content;
        private readonly int _statusCode;

        public string Content => _content;
        public int StatusCode => _statusCode;

        public ResponseBase()
        {
            _content = string.Empty;
            _statusCode = 500;
        }

        public ResponseBase(string content)
        {
            _content = content ?? string.Empty;
        }

        public ResponseBase(string content, int statusCode)
        {
            _content = content ?? string.Empty;
            _statusCode = statusCode;
        }
    }
}

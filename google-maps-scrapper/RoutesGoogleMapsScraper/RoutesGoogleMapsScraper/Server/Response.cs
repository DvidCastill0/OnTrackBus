namespace RoutesGoogleMapsScraper.Server
{
    public class Response : ResponseBase
    {
        public Response() : base()
        {
        }

        public Response(string content) : base(content)
        {
        }

        public Response(string content, int statusCode) : base(content, statusCode)
        {
        }
    }
}

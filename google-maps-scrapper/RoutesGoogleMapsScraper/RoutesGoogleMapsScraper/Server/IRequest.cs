using System.Net.Http.Headers;

namespace RoutesGoogleMapsScraper.Server
{
    public interface IRequest
    {
        public Task<string> GetBody();
        public string GetMethod();
        public List<Header> GetHeaders();
        public string GetRequestData();
    }
}

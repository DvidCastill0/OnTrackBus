using AutoMapper;
using Serilog;

namespace RoutesGoogleMapsScraper.Server
{
    public interface IWebServer
    {
        public void Start(ILogger logger, IMapper mapper);
        public void Stop();
        public Response SendDataToController(ILogger logger, IMapper mapper, Request request);
    }
}

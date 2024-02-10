using Newtonsoft.Json;
namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusRouteReadDTO
    {
        public string Name { get; set; }
        public Dictionary<string, OnTrackBusStandReadDTO> Outgoing { get; set; }
        public Dictionary<string, OnTrackBusStandReadDTO> Incoming { get; set; }
    }
}

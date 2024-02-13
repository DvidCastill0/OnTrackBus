using Newtonsoft.Json;
namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusRouteReadDTO
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public Dictionary<string, OnTrackBusStandReadDTO> Outgoing { get; set; }
        public Dictionary<string, OnTrackBusStandReadDTO> Incoming { get; set; }
    }
}

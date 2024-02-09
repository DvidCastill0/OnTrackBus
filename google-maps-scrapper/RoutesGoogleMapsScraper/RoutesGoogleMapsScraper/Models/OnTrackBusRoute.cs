using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusRoute
    {
        [JsonProperty("NombreDeRuta")]
        public string Name { get; set; }
        [JsonProperty("ida")]
        public Dictionary<string, OnTrackBusStand> Outgoing { get; set; }
        [JsonProperty("vuelta")]
        public Dictionary<string, OnTrackBusStand> Incoming { get; set; }

        public OnTrackBusRoute()
        {
            Name = string.Empty;
            Outgoing = new Dictionary<string, OnTrackBusStand>();
            Incoming = new Dictionary<string, OnTrackBusStand>();
        }
    }
}

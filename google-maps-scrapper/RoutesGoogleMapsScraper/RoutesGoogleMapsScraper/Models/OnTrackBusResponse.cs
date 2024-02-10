using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusResponse
    {
        [JsonProperty("Canales")]
        public Dictionary<string, OnTrackBusChannel> Channels { get; set; }
        [JsonProperty("DudasSoporte")]
        public Dictionary<string, OnTrackBusSupportQuestion> SupportQuestions { get; set; }
        [JsonProperty("Rutas")]
        public Dictionary<string, OnTrackBusRoute> Routes { get; set; }
        [JsonProperty("Users")]
        public Dictionary<string, OnTrackBusUser> Users { get; set; }

        public OnTrackBusResponse()
        {
            Channels = new Dictionary<string, OnTrackBusChannel>();
            SupportQuestions = new Dictionary<string, OnTrackBusSupportQuestion>();
            Routes = new Dictionary<string, OnTrackBusRoute>();
            Users = new Dictionary<string, OnTrackBusUser>();
        }
    }
}

using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusSelectedChannel
    {
        [JsonProperty("Id_CanalSeleccionado")]
        public int Id { get; set; }
        [JsonProperty("Nombre")]
        public string Name { get; set; }

        public OnTrackBusSelectedChannel()
        {
            Id = 0;
            Name = string.Empty;
        }
    }
}

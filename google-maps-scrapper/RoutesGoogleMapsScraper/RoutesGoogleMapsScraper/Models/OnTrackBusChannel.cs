using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusChannel
    {
        [JsonProperty("NombreCanal")]
        public string Name;
        [JsonProperty("Reportes")]
        public Dictionary<string, OnTrackBusReport> Reports;
        [JsonProperty("Sugerencias")]
        public Dictionary<string, OnTrackBusSuggestion> Suggestions;
        [JsonProperty("Tarifa")]
        public float Price;
        public OnTrackBusChannel()
        {
            Name = string.Empty;
            Reports = new Dictionary<string, OnTrackBusReport>();
            Suggestions = new Dictionary<string, OnTrackBusSuggestion>();
            Price = 0;
        }
    }
}

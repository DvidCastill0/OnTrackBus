using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusStand
    {
        [JsonProperty("tittle")]
        public string Name { get; set; }
        [JsonProperty("latitud")]
        public float Latitude { get; set; }
        [JsonProperty("longitud")]
        public float Longitude { get; set; }
        [JsonProperty("IAValue")]
        public string Prediction { get; set; }
        [JsonProperty("snnipet")]
        public string LastTimeBoarded { get; set; }

        public OnTrackBusStand()
        {
            Name = string.Empty;
            Latitude = 0;
            Longitude = 0;
            Prediction = DateTime.Now.ToString("HH:mm;ss d-MM-yyyy");
            LastTimeBoarded = DateTime.Now.ToString("HH:mm:ss  d-MM-yyyy");
        }

    }
}

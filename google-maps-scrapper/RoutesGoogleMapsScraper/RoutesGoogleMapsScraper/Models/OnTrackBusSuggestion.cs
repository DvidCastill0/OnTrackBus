using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusSuggestion
    {
        [JsonProperty("Id_ClaveSugerencia")]
        public string Id { get; set; }
        [JsonProperty("CorreoRemitente")]
        public string Email { get; set; }
        [JsonProperty("FechaDeEmision")]
        public string IssuedDateTime { get; set; }
        [JsonProperty("sugerencia_Contenido")]
        public string Content { get; set; }

        public OnTrackBusSuggestion()
        {
            Id = string.Empty;
            Email = string.Empty;
            Content = string.Empty;
            IssuedDateTime = DateTime.Now.ToString();
        }
    }
}

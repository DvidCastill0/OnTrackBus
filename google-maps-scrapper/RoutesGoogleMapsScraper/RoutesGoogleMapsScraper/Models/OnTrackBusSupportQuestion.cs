using Newtonsoft.Json;

namespace RoutesGoogleMapsScraper.Models
{
    public class OnTrackBusSupportQuestion
    {
        [JsonProperty("contenidoDuda")]
        public string Content;
        [JsonProperty("correoRemitente")]
        public string Email;
        [JsonProperty("fechaDeRemision")]
        public string IssuedDateTime;

        public OnTrackBusSupportQuestion()
        {
            Content = string.Empty;
            Email = string.Empty;
            IssuedDateTime = DateTime.Now.ToString("HH:mm:ss d-MM-yyyy");
        }
    }
}
